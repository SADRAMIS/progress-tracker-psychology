package com.ramis.progresstracker.service;

import com.ramis.progresstracker.entity.ProgressRecord;
import com.ramis.progresstracker.entity.User;
import com.ramis.progresstracker.repository.ProgressRecordRepository;
import com.ramis.progresstracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    private final ProgressRecordRepository progressRecordRepository;
    private final UserRepository userRepository;

    /**
     * Получить месячную аналитику
     */
    public Map<String, Object> getMonthlyAnalytics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate monthAgo = LocalDate.now().minusMonths(1);
        List<ProgressRecord> records = progressRecordRepository
                .findByUserIdAndDateRange(userId, monthAgo, LocalDate.now());

        Map<String, Object> analytics = new HashMap<>();

        int totalXP = records.stream().mapToInt(ProgressRecord::getXpGained).sum();
        int totalQuestions = records.stream().mapToInt(ProgressRecord::getQuestionsSolved).sum();
        double totalHours = records.stream().mapToDouble(ProgressRecord::getHoursStudied).sum();
        double avgAccuracy = records.stream().mapToDouble(ProgressRecord::getPercentageCorrect).average().orElse(0);

        analytics.put("totalXP", totalXP);
        analytics.put("totalQuestions", totalQuestions);
        analytics.put("totalHours", String.format("%.1f", totalHours));
        analytics.put("avgAccuracy", String.format("%.1f", avgAccuracy));
        analytics.put("daysActive", records.size());
        analytics.put("avgXPPerDay", String.format("%.0f", (double) totalXP / 30));

        return analytics;
    }

    /**
     * Сравни неделю с неделей
     */
    public Map<String, Object> getWeeklyComparison(Long userId) {
        LocalDate currentWeekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDate previousWeekStart = currentWeekStart.minusDays(7);

        Integer currentWeekXP = progressRecordRepository.getTotalXPByDateRange(
                userId, currentWeekStart, LocalDate.now());

        Integer previousWeekXP = progressRecordRepository.getTotalXPByDateRange(
                userId, previousWeekStart, currentWeekStart.minusDays(1));

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("currentWeekXP", currentWeekXP != null ? currentWeekXP : 0);
        comparison.put("previousWeekXP", previousWeekXP != null ? previousWeekXP : 0);
        comparison.put("change", ((currentWeekXP != null ? currentWeekXP : 0) - (previousWeekXP != null ? previousWeekXP : 0)));

        double percentChange = previousWeekXP != null && previousWeekXP > 0
                ? (((currentWeekXP != null ? currentWeekXP : 0) - previousWeekXP) * 100.0) / previousWeekXP
                : 0;
        comparison.put("percentChange", String.format("%.1f", percentChange));

        return comparison;
    }

    /**
     * Генерируй прогноз на основе текущего темпа
     */
    public Map<String, Object> generateForecast(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate weekAgo = LocalDate.now().minusDays(7);
        Integer weeklyXP = progressRecordRepository.getTotalXPByDateRange(
                userId, weekAgo, LocalDate.now());

        weeklyXP = weeklyXP != null ? weeklyXP : 100;

        int monthlyForecast = weeklyXP * 4;
        int nextLevelXP = user.getXPToNextLevel();
        double daysToNextLevel = (double) nextLevelXP / (weeklyXP / 7.0);

        Map<String, Object> forecast = new HashMap<>();
        forecast.put("weeklyForecast", weeklyXP);
        forecast.put("monthlyForecast", monthlyForecast);
        forecast.put("daysToNextLevel", String.format("%.1f", daysToNextLevel));
        forecast.put("nextLevelDate", LocalDate.now().plusDays((long) daysToNextLevel));

        return forecast;
    }
}
