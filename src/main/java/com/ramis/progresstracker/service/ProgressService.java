package com.ramis.progresstracker.service;

import com.ramis.progresstracker.dto.*;
import com.ramis.progresstracker.entity.*;
import com.ramis.progresstracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Сервис для управления прогрессом пользователя
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressService {
    private final ProgressRecordRepository progressRecordRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    /**
     * Отправить ответ на вопрос и обновить прогресс
     */
    public int submitAnswer(QuestionAnswerDTO dto) {
        // Получи user и question
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // Создай или обнови Answer
        Answer answer = answerRepository.findByUserIdAndQuestionId(user.getId(), question.getId())
                .orElse(new Answer());

        answer.setUser(user);
        answer.setQuestion(question);
        answer.setLevelCompleted(dto.getLevelCompleted());
        answer.setAttempts(dto.getAttempts());
        answer.setTimeSpentMinutes(dto.getTimeSpentMinutes());
        answer.setNotes(dto.getNotes());
        answer.setIsCorrect(dto.getLevelCompleted() == 3);

        Answer savedAnswer = answerRepository.save(answer);

        // Вычисли XP
        int xpGained = calculateXPGained(savedAnswer);

        // Обнови User XP
        user.setTotalXP(user.getTotalXP() + xpGained);
        user.setLevel(user.calculateLevel());
        userRepository.save(user);

        // Обнови прогресс за день
        updateDailyProgress(user, xpGained, dto.getTimeSpentMinutes());

        log.info("Answer submitted: user={}, question={}, xp={}", user.getId(), question.getId(), xpGained);
        return xpGained;
    }

    /**
     * Получить прогресс за дату
     */
    public ProgressDTO getProgressByDate(Long userId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<ProgressRecord> record = progressRecordRepository.findByUserId(userId).stream()
                .filter(p -> p.getDate().equals(date))
                .findFirst();

        ProgressDTO dto = new ProgressDTO();
        if (record.isPresent()) {
            ProgressRecord pr = record.get();
            dto.setDate(pr.getDate());
            dto.setXpGained(pr.getXpGained());
            dto.setHoursStudied(pr.getHoursStudied());
            dto.setQuestionsSolved(pr.getQuestionsSolved());
            dto.setPercentageCorrect(pr.getPercentageCorrect());
            dto.setStreakDays(pr.getStreakDays());
            dto.setMoodRating(pr.getMoodRating());
            dto.setSummary(generateSummary(pr));
        } else {
            dto.setDate(date);
            dto.setSummary("No activity this day");
        }

        return dto;
    }

    /**
     * Получить прогресс за период
     */
    public List<ProgressDTO> getProgressRange(Long userId, String startDateStr, String endDateStr) {
        LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_DATE);

        List<ProgressRecord> records = progressRecordRepository
                .findByUserIdAndDateRange(userId, startDate, endDate);

        List<ProgressDTO> result = new ArrayList<>();
        for (ProgressRecord record : records) {
            ProgressDTO dto = new ProgressDTO();
            dto.setDate(record.getDate());
            dto.setXpGained(record.getXpGained());
            dto.setHoursStudied(record.getHoursStudied());
            dto.setQuestionsSolved(record.getQuestionsSolved());
            dto.setPercentageCorrect(record.getPercentageCorrect());
            dto.setStreakDays(record.getStreakDays());
            dto.setMoodRating(record.getMoodRating());
            dto.setSummary(generateSummary(record));
            result.add(dto);
        }

        return result;
    }

    /**
     * Получить еженедельную статистику
     */
    public Map<String, Object> getWeeklyStatistics(Long userId) {
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        List<ProgressRecord> weeklyRecords = progressRecordRepository
                .findByUserIdAndDateRange(userId, weekAgo, LocalDate.now());

        Map<String, Object> stats = new HashMap<>();

        int totalXP = weeklyRecords.stream()
                .mapToInt(ProgressRecord::getXpGained)
                .sum();

        double totalHours = weeklyRecords.stream()
                .mapToDouble(ProgressRecord::getHoursStudied)
                .sum();

        int totalQuestions = weeklyRecords.stream()
                .mapToInt(ProgressRecord::getQuestionsSolved)
                .sum();

        double avgAccuracy = weeklyRecords.stream()
                .mapToDouble(ProgressRecord::getPercentageCorrect)
                .average()
                .orElse(0);

        stats.put("totalXP", totalXP);
        stats.put("totalHours", String.format("%.1f", totalHours));
        stats.put("totalQuestions", totalQuestions);
        stats.put("avgAccuracy", String.format("%.1f", avgAccuracy));
        stats.put("daysActive", weeklyRecords.size());

        return stats;
    }

    /**
     * Вычисли XP за ответ
     */
    private int calculateXPGained(Answer answer) {
        int baseXP = answer.getQuestion().getXpValue();
        double multiplier = answer.getLevelCompleted() / 3.0; // Уровень 3 = 100% XP
        return (int) (baseXP * multiplier);
    }

    /**
     * Обнови ежедневный прогресс
     */
    private void updateDailyProgress(User user, int xpGained, Integer timeSpent) {
        LocalDate today = LocalDate.now();

        Optional<ProgressRecord> existingRecord = progressRecordRepository.findByUserId(user.getId()).stream()
                .filter(p -> p.getDate().equals(today))
                .findFirst();

        ProgressRecord record;
        if (existingRecord.isPresent()) {
            record = existingRecord.get();
            record.setXpGained(record.getXpGained() + xpGained);
            record.setQuestionsSolved(record.getQuestionsSolved() + 1);
            if (timeSpent != null) {
                record.setHoursStudied(record.getHoursStudied() + timeSpent / 60.0);
            }
        } else {
            record = new ProgressRecord();
            record.setUser(user);
            record.setDate(today);
            record.setXpGained(xpGained);
            record.setQuestionsSolved(1);
            record.setStreakDays(calculateStreak(user.getId()));
            if (timeSpent != null) {
                record.setHoursStudied(timeSpent / 60.0);
            }
        }

        progressRecordRepository.save(record);
    }

    /**
     * Вычисли текущий streak дней
     */
    private int calculateStreak(Long userId) {
        List<ProgressRecord> records = progressRecordRepository.findByUserId(userId);

        if (records.isEmpty()) {
            return 1;
        }

        records.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        int streak = 0;
        LocalDate currentDate = LocalDate.now();

        for (ProgressRecord record : records) {
            if (record.getDate().equals(currentDate)) {
                streak++;
                currentDate = currentDate.minusDays(1);
            } else if (record.getDate().equals(currentDate.minusDays(1))) {
                streak++;
                currentDate = currentDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * Генерируй summary за день
     */
    private String generateSummary(ProgressRecord record) {
        return String.format(
                "XP: %d | Hours: %.1f | Questions: %d | Accuracy: %.1f%% | Streak: %d days",
                record.getXpGained(),
                record.getHoursStudied(),
                record.getQuestionsSolved(),
                record.getPercentageCorrect(),
                record.getStreakDays()
        );
    }
}
