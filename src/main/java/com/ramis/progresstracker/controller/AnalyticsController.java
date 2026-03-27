package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Получить полную аналитику за месяц
     * GET /api/analytics/user/{userId}/monthly
     */
    @GetMapping("/user/{userId}/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyAnalytics(@PathVariable Long userId) {
        Map<String, Object> analytics = analyticsService.getMonthlyAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Получить сравнение текущей недели с предыдущей
     * GET /api/analytics/user/{userId}/weekly-comparison
     */
    @GetMapping("/user/{userId}/weekly-comparison")
    public ResponseEntity<Map<String, Object>> getWeeklyComparison(@PathVariable Long userId) {
        Map<String, Object> comparison = analyticsService.getWeeklyComparison(userId);
        return ResponseEntity.ok(comparison);
    }

    /**
     * Получить прогноз прогресса
     * GET /api/analytics/user/{userId}/forecast
     */
    @GetMapping("/user/{userId}/forecast")
    public ResponseEntity<Map<String, Object>> getForecast(@PathVariable Long userId) {
        Map<String, Object> forecast = analyticsService.generateForecast(userId);
        return ResponseEntity.ok(forecast);
    }

}
