package com.ramis.progresstracker.service;

import com.ramis.progresstracker.dto.*;
import com.ramis.progresstracker.entity.*;
import com.ramis.progresstracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис для генерации AI рекомендаций на основе данных пользователя
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendationService {
    private final PsychometricScoreRepository psychometricScoreRepository;
    private final ProgressRecordRepository progressRecordRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    /**
     * Генерирует персонализированные рекомендации для пользователя
     */
    public List<RecommendationDTO> generateRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<RecommendationDTO> recommendations = new ArrayList<>();

        // 1. Рекомендация по психометрике
        Optional<PsychometricScore> latestScore = psychometricScoreRepository.getLatestScoreByUserId(userId);
        if (latestScore.isPresent()) {
            PsychometricScore score = latestScore.get();

            if (score.getMotivation() < 40) {
                recommendations.add(createMotivationRecommendation());
            }
            if (score.getFocus() < 50) {
                recommendations.add(createFocusRecommendation());
            }
            if (score.getConsistency() < 30) {
                recommendations.add(createConsistencyRecommendation());
            }
            if (score.getConfidence() < 50 && score.getResilience() < 50) {
                recommendations.add(createLearningPathRecommendation());
            }
        }

        // 2. Анализ слабых мест
        recommendations.add(analyzeWeakAreas(userId));

        // 3. Анализ тренда прогресса
        recommendations.add(analyzeProgressTrend(userId));

        log.info("Generated {} recommendations for user {}", recommendations.size(), userId);
        return recommendations;
    }

    private RecommendationDTO createMotivationRecommendation() {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setType("MOTIVATION");
        dto.setContent("Ваша мотивация снизилась. Пора обновить цели!");
        dto.setPriority("HIGH");
        dto.setActionItems("1. Пересмотрите свои долгосрочные цели\n" +
                "2. Разбейте их на небольшие еженедельные задачи\n" +
                "3. Вознаградите себя за достижения\n" +
                "4. Присоединитесь к сообществу учащихся");
        dto.setRead(false);
        return dto;
    }

    private RecommendationDTO createFocusRecommendation() {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setType("FOCUS");
        dto.setContent("Ваша концентрация требует улучшения");
        dto.setPriority("MEDIUM");
        dto.setActionItems("1. Используйте метод Pomodoro (25 мин работа + 5 мин отдых)\n" +
                "2. Отключите уведомления во время учёбы\n" +
                "3. Выберите тихое место для занятий\n" +
                "4. Делайте небольшие перерывы каждый час");
        dto.setRead(false);
        return dto;
    }

    private RecommendationDTO createConsistencyRecommendation() {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setType("CONSISTENCY");
        dto.setContent("Занимайтесь регулярно, даже если по 30 минут");
        dto.setPriority("HIGH");
        dto.setActionItems("1. Установите ежедневное время для учёбы\n" +
                "2. Создайте напоминание на телефон\n" +
                "3. Ведите дневник прогресса\n" +
                "4. Найдите партнёра по учёбе для взаимоподдержки");
        dto.setRead(false);
        return dto;
    }

    private RecommendationDTO createLearningPathRecommendation() {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setType("LEARNING_PATH");
        dto.setContent("Рекомендуемый путь обучения на следующую неделю");
        dto.setPriority("MEDIUM");
        dto.setActionItems("1. ПН-ВТ: Повторите базовые концепции Java (Вопросы 1-5)\n" +
                "2. СР-ЧТ: Изучите ООП принципы (Вопросы 3-4)\n" +
                "3. ПТ-СБ: Практикуйте Spring Boot (Вопрос 11)\n" +
                "4. ВС: Повторение и анализ ошибок");
        dto.setRead(false);
        return dto;
    }

    /**
     * Анализирует слабые места на основе ошибок
     */
    private RecommendationDTO analyzeWeakAreas(Long userId) {
        List<Answer> answers = answerRepository.findByUserId(userId);

        Map<String, Long> errorsByCategory = new HashMap<>();
        answers.stream()
                .filter(a -> !a.getIsCorrect())
                .forEach(a -> errorsByCategory.merge(
                        a.getQuestion().getCategory(), 1L, Long::sum));

        String weakestCategory = errorsByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");

        RecommendationDTO dto = new RecommendationDTO();
        dto.setType("LEARNING_PATH");
        dto.setContent("Вы делаете много ошибок в категории: " + weakestCategory);
        dto.setPriority("HIGH");
        dto.setActionItems("Рекомендуется повторить теорию и практику для " + weakestCategory);
        dto.setRead(false);
        return dto;
    }

    /**
     * Анализирует тренд прогресса
     */
    private RecommendationDTO analyzeProgressTrend(Long userId) {
        LocalDate twoWeeksAgo = LocalDate.now().minusDays(14);
        List<ProgressRecord> recentRecords = progressRecordRepository
                .findByUserIdAndDateRange(userId, twoWeeksAgo, LocalDate.now());

        if (recentRecords.isEmpty()) {
            return createNoActivityRecommendation();
        }

        // Безопасное получение XP (null-safe)
        Integer xpLastWeek = getTotalXPByDateRangeSafe(userId, LocalDate.now().minusDays(7), LocalDate.now());
        Integer xpPreviousWeek = getTotalXPByDateRangeSafe(userId, LocalDate.now().minusDays(14), LocalDate.now().minusDays(7));

        RecommendationDTO dto = new RecommendationDTO();
        dto.setType("MOTIVATION");

        if (xpLastWeek > xpPreviousWeek * 1.2) {
            dto.setContent("🚀 Отличный прогресс! Вы набираете скорость!");
            dto.setPriority("LOW");
            dto.setActionItems("Поддерживайте этот темп. Вы на правильном пути!");
        } else if (xpPreviousWeek != null && xpPreviousWeek > 0 && xpLastWeek < xpPreviousWeek * 0.8) {
            dto.setContent("⚠️ Прогресс замедлился на 20%");
            dto.setPriority("HIGH");
            dto.setActionItems("Увеличьте количество часов обучения на 30% на следующую неделю");
        } else {
            dto.setContent("Прогресс стабилен");
            dto.setPriority("MEDIUM");
            dto.setActionItems("Все идёт хорошо. Продолжайте текущий темп.");
        }
        dto.setRead(false);
        return dto;
    }

    private RecommendationDTO createNoActivityRecommendation() {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setType("CONSISTENCY");
        dto.setContent("Нет активности за последние 2 недели");
        dto.setPriority("HIGH");
        dto.setActionItems("Начните с малого: решите хотя бы 1 вопрос сегодня!");
        dto.setRead(false);
        return dto;
    }

    // Вспомогательный метод для null-safe XP
    private Integer getTotalXPByDateRangeSafe(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            Integer xp = progressRecordRepository.getTotalXPByDateRange(userId, startDate, endDate);
            return xp != null ? xp : 0;
        } catch (Exception e) {
            log.warn("Error getting XP for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }
}