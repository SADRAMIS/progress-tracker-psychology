package com.ramis.progresstracker.service;

import com.ramis.progresstracker.entity.*;
import com.ramis.progresstracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {
    private final GoalRepository goalRepository;
    private final ProgressRecordRepository progressRecordRepository;
    private final UserRepository userRepository;

    /**
     * Получить уведомления для пользователя
     */
    public List<String> getNotifications(Long userId) {
        List<String> notifications = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // 1. Просроченные цели
        List<Goal> overdueGoals = goalRepository.findByUserIdAndDeadlineBeforeOrderByDeadlineAsc(userId, tomorrow);
        for (Goal goal : overdueGoals) {
            notifications.add("⏰ Цель '" + goal.getTitle() + "' просрочена! 🕐 " + goal.getDeadline());
        }

        // 2. Нет активности 3+ дня
        List<ProgressRecord> recentRecords = progressRecordRepository.findByUserId(userId);
        if (!recentRecords.isEmpty()) {
            recentRecords.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            LocalDate lastActivity = recentRecords.get(0).getDate();
            long daysInactive = java.time.temporal.ChronoUnit.DAYS.between(lastActivity, LocalDate.now());
            if (daysInactive >= 3) {
                notifications.add("💤 Нет активности " + daysInactive + " дней. Начните с 1 вопроса!");
            }
        }

        // 3. Цели близко к дедлайну (3 дня)
        List<Goal> urgentGoals = goalRepository.findByUserIdAndDeadlineBeforeOrderByDeadlineAsc(userId, LocalDate.now().plusDays(3));
        for (Goal goal : urgentGoals) {
            if (goal.getStatus() == Goal.GoalStatus.ACTIVE) {
                notifications.add("⚡ '" + goal.getTitle() + "' дедлайн через " +
                        java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), goal.getDeadline()) + " дней!");
            }
        }

        log.info("Generated {} notifications for user {}", notifications.size(), userId);
        return notifications;
    }
}
