package com.ramis.progresstracker.service;

import com.ramis.progresstracker.dto.*;
import com.ramis.progresstracker.entity.*;
import com.ramis.progresstracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для психометрического анализа прогресса пользователя
 * Анализирует мотивацию, уверенность, стрессоустойчивость, концентрацию
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PsychometricAnalysisService {
    private final PsychometricScoreRepository psychometricScoreRepository;
    private final ProgressRecordRepository progressRecordRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    /**
     * Анализирует психологический профиль пользователя на основе его действий
     */
    public PsychometricDTO analyzePsychologicalProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Получаем данные за последние 30 дней
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<ProgressRecord> records = progressRecordRepository
                .findByUserIdAndDateRange(userId, thirtyDaysAgo, LocalDate.now());

        List<Answer> answers = answerRepository.findByUserId(userId);

        PsychometricDTO dto = new PsychometricDTO();

        // 1. MOTIVATION (Мотивация) - на основе регулярности занятий и XP gains
        dto.setMotivation(calculateMotivation(records));

        // 2. CONFIDENCE (Уверенность) - на основе процента правильных ответов
        dto.setConfidence(calculateConfidence(answers));

        // 3. RESILIENCE (Стрессоустойчивость) - как часто восстанавливается после ошибок
        dto.setResilience(calculateResilience(records, answers));

        // 4. FOCUS (Концентрация) - на основе времени, потраченного на задачу, и ошибок
        dto.setFocus(calculateFocus(records, answers));

        // 5. CONSISTENCY (Постоянство) - длина streak дней
        dto.setConsistency(calculateConsistency(records));

        // Общий балл
        Double overallScore = (dto.getMotivation() + dto.getConfidence() +
                dto.getResilience() + dto.getFocus() +
                dto.getConsistency()) / 5.0;
        dto.setOverallScore(overallScore);

        // Создаём анализ
        dto.setAnalysis(generatePsychologicalAnalysis(dto, records, answers));
        dto.setRecommendation(generateRecommendation(dto));

        // Сохраняем результаты в БД
        PsychometricScore score = new PsychometricScore();
        score.setUser(user);
        score.setMotivation(dto.getMotivation());
        score.setConfidence(dto.getConfidence());
        score.setResilience(dto.getResilience());
        score.setFocus(dto.getFocus());
        score.setConsistency(dto.getConsistency());
        score.setOverallScore(overallScore);
        psychometricScoreRepository.save(score);

        log.info("Psychometric analysis completed for user {}", userId);
        return dto;

    }
    /**
     * Мотивация (0-100)
     * Основана на: количество дней занятий, XP gains, прогрессе к целям
     */
    private Double calculateMotivation(List<ProgressRecord> records) {
        if (records.isEmpty()) {
            return 30.0; // Low motivation if no recent activity
        }

        // Сколько дней за последние 30 активен?
        long activeDays = records.stream()
                .filter(r -> r.getXpGained() > 0)
                .count();

        double dayRatio = (activeDays / 30.0) * 60; // Max 60 points

        // Average XP gained per day
        double avgXPPerDay = records.stream()
                .mapToInt(ProgressRecord::getXpGained)
                .average()
                .orElse(0);

        double xpBonus = Math.min((avgXPPerDay / 50.0) * 40, 40); // Max 40 points

        return Math.min(dayRatio + xpBonus, 100.0);
    }

    /**
     * Уверенность (0-100)
     * На основе процента правильных ответов и сложности решённых задач
     */
    private Double calculateConfidence(List<Answer> answers) {
        if (answers.isEmpty()) {
            return 50.0;
        }

        long correctAnswers = answers.stream()
                .filter(Answer::getIsCorrect)
                .count();

        double correctPercentage = (correctAnswers * 100.0) / answers.size();

        // Сложность решённых задач даёт бонус
        double hardTaskBonus = answers.stream()
                .filter(a -> a.getQuestion().getDifficulty() == Question.Difficulty.HARD && a.getIsCorrect())
                .count() * 2;

        return Math.min(correctPercentage + Math.min(hardTaskBonus, 20), 100.0);
    }

    /**
     * Стрессоустойчивость (0-100)
     * На основе: восстановления после ошибок, настроения, приверженности
     */
    private Double calculateResilience(List<ProgressRecord> records, List<Answer> answers) {
        if (records.isEmpty() || answers.isEmpty()) {
            return 50.0;
        }

        // Если есть ошибки, но пользователь продолжает → высокая стрессоустойчивость
        long errorsCount = answers.stream()
                .filter(a -> !a.getIsCorrect())
                .count();

        long recoveryAttempts = answers.stream()
                .filter(a -> a.getAttempts() > 1)
                .count();

        double recoveryRate = errorsCount > 0 ? (recoveryAttempts * 100.0) / errorsCount : 50.0;

        // Среднее настроение (1-5 → переводим в 0-100)
        double avgMood = records.stream()
                .filter(r -> r.getMoodRating() != null)
                .mapToInt(ProgressRecord::getMoodRating)
                .average()
                .orElse(3) * 20; // 3/5 = 60/100

        return (recoveryRate * 0.6 + avgMood * 0.4);
    }

    /**
     * Концентрация (0-100)
     * На основе: времени на задачу, количества попыток, точности ответов
     */
    private Double calculateFocus(List<ProgressRecord> records, List<Answer> answers) {
        if (records.isEmpty() || answers.isEmpty()) {
            return 50.0;
        }

        // Среднее время на задачу (в идеале 15-30 минут)
        double avgTimePerQuestion = answers.stream()
                .filter(a -> a.getTimeSpentMinutes() != null)
                .mapToInt(Answer::getTimeSpentMinutes)
                .average()
                .orElse(20);

        // Если слишком быстро (< 5 мин) или слишком долго (> 60 мин) → низкая концентрация
        double timeScore = 100 - Math.abs(avgTimePerQuestion - 20) * 2;
        timeScore = Math.max(Math.min(timeScore, 100), 20);

        // Среднее количество попыток (идеально 1-2)
        double avgAttempts = answers.stream()
                .mapToInt(Answer::getAttempts)
                .average()
                .orElse(2);

        double attemptScore = Math.min(100 / avgAttempts * 1.5, 100);

        return (timeScore * 0.5 + attemptScore * 0.5);
    }

    /**
     * Постоянство (0-100)
     * На основе streak дней и регулярности
     */
    private Double calculateConsistency(List<ProgressRecord> records) {
        if (records.isEmpty()) {
            return 20.0;
        }

        long maxStreak = records.stream()
                .mapToInt(ProgressRecord::getStreakDays)
                .max()
                .orElse(0);

        // Max 100 points за 30-дневный streak
        return Math.min((maxStreak / 30.0) * 100, 100.0);
    }

    /**
     * Генерирует подробный психологический анализ
     */
    private String generatePsychologicalAnalysis(PsychometricDTO dto,
                                                 List<ProgressRecord> records,
                                                 List<Answer> answers) {
        StringBuilder analysis = new StringBuilder();

        analysis.append("=== ПСИХОЛОГИЧЕСКИЙ АНАЛИЗ ===\n\n");

        analysis.append("📊 ИТОГОВЫЕ БАЛЛЫ:\n");
        analysis.append(String.format("• Мотивация: %.1f%%\n", dto.getMotivation()));
        analysis.append(String.format("• Уверенность: %.1f%%\n", dto.getConfidence()));
        analysis.append(String.format("• Стрессоустойчивость: %.1f%%\n", dto.getResilience()));
        analysis.append(String.format("• Концентрация: %.1f%%\n", dto.getFocus()));
        analysis.append(String.format("• Постоянство: %.1f%%\n\n", dto.getConsistency()));

        analysis.append("💡 ИНТЕРПРЕТАЦИЯ:\n");

        if (dto.getMotivation() > 75) {
            analysis.append("✅ Вы очень мотивированы! Продолжайте в том же духе.\n");
        } else if (dto.getMotivation() > 50) {
            analysis.append("⚠️  Мотивация выше среднего. Можно усилить регулярность.\n");
        } else {
            analysis.append("❌ Мотивация низкая. Рекомендуется установить более реалистичные цели.\n");
        }

        if (dto.getConfidence() > 75) {
            analysis.append("✅ Высокая уверенность в своих знаниях!\n");
        } else if (dto.getConfidence() < 50) {
            analysis.append("❌ Низкая уверенность. Рекомендуется повторить базовые концепции.\n");
        }

        if (dto.getResilience() > 75) {
            analysis.append("✅ Отличная стрессоустойчивость - вы быстро восстанавливаетесь после ошибок!\n");
        } else if (dto.getResilience() < 40) {
            analysis.append("⚠️  Низкая стрессоустойчивость. Попробуйте разбить задачи на более мелкие.\n");
        }

        if (dto.getFocus() > 80) {
            analysis.append("✅ Отличная концентрация внимания!\n");
        } else if (dto.getFocus() < 50) {
            analysis.append("⚠️  Низкая концентрация. Рекомендуется избегать отвлечений.\n");
        }

        if (dto.getConsistency() > 75) {
            analysis.append("✅ Превосходное постоянство! Вы дисциплинированны.\n");
        } else if (dto.getConsistency() < 30) {
            analysis.append("❌ Низкое постоянство. Старайтесь занимаются каждый день, даже 30 минут.\n");
        }

        return analysis.toString();
    }

    /**
     * Генерирует рекомендацию на основе анализа
     */
    private String generateRecommendation(PsychometricDTO dto) {
        // Найди самый низкий балл
        Double minScore = Math.min(
                Math.min(Math.min(dto.getMotivation(), dto.getConfidence()),
                        Math.min(dto.getResilience(), dto.getFocus())),
                dto.getConsistency()
        );

        if (minScore.equals(dto.getMotivation())) {
            return "🎯 РЕКОМЕНДАЦИЯ: Установите более амбициозную, но достижимую цель. Разбейте её на подзадачи на неделю.";
        } else if (minScore.equals(dto.getConfidence())) {
            return "📚 РЕКОМЕНДАЦИЯ: Повторите фундаментальные концепции. Начните с более лёгких задач (EASY уровень).";
        } else if (minScore.equals(dto.getResilience())) {
            return "💪 РЕКОМЕНДАЦИЯ: Практикуйте самосострадание. Ошибки - это часть процесса обучения. Разбейте задачи на более мелкие.";
        } else if (minScore.equals(dto.getFocus())) {
            return "🎯 РЕКОМЕНДАЦИЯ: Используйте метод Pomodoro (25 мин работы + 5 мин перерыв). Уберите отвлечения.";
        } else {
            return "📅 РЕКОМЕНДАЦИЯ: Занимайтесь хотя бы 30 минут каждый день. Установите напоминание для повседневной учёбы.";
        }
    }
}