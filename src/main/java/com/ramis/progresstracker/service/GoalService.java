package com.ramis.progresstracker.service;

import com.ramis.progresstracker.controller.GoalController;
import com.ramis.progresstracker.dto.GoalDTO;
import com.ramis.progresstracker.entity.Goal;
import com.ramis.progresstracker.entity.User;
import com.ramis.progresstracker.repository.GoalRepository;
import com.ramis.progresstracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalDTO createGoal(Long userId, String title, String description, Integer xpTarget) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle(title);
        goal.setDescription(description);
        goal.setXpTarget(xpTarget);
        goal.setCurrentXP(0);
        goal.setStatus(Goal.GoalStatus.ACTIVE);

        Goal saved = goalRepository.save(goal);
        log.info("Goal created: {} for user {}", title, userId);
        return convertToDTO(saved);
    }

    public List<GoalDTO> getUserGoals(Long userId) {
        return goalRepository.findByUserIdOrderByCreatedDateDesc(userId).stream()  // Используй метод из repo
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GoalDTO completeGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goal.setStatus(Goal.GoalStatus.COMPLETED);
        Goal saved = goalRepository.save(goal);
        log.info("Goal completed: {}", goalId);
        return convertToDTO(saved);
    }

    private GoalDTO convertToDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setXpTarget(goal.getXpTarget());
        dto.setCurrentXP(goal.getCurrentXP());
        dto.setStatus(goal.getStatus().name());
        dto.setProgressPercentage(goal.getProgressPercentage());
        if (goal.getDeadline() != null) {
            dto.setDeadline(goal.getDeadline().toString());
        }
        return dto;
    }

    public GoalDTO updateGoal(Long goalId, GoalController.UpdateGoalRequest request) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        // Обновляем только те поля, которые реально пришли в запросе
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            goal.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }

        if (request.getCurrentXP() != null) {
            goal.setCurrentXP(request.getCurrentXP());
            // Опционально: если достигнут target, помечаем как COMPLETED
            if (goal.getCurrentXP() >= goal.getXpTarget()) {
                goal.setStatus(Goal.GoalStatus.COMPLETED);
            }
        }

        Goal saved = goalRepository.save(goal);
        log.info("Goal updated: {} (title={}, currentXP={})",
                goalId, saved.getTitle(), saved.getCurrentXP());

        return convertToDTO(saved);
    }
}