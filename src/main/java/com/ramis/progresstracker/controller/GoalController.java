package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.dto.GoalDTO;
import com.ramis.progresstracker.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GoalController {

    private final GoalService goalService;

    /**
     * Создать новую цель
     * POST /api/goals
     */
    @PostMapping
    public ResponseEntity<GoalDTO> createGoal(@RequestBody CreateGoalRequest request) {
        GoalDTO goal = goalService.createGoal(
                request.getUserId(),
                request.getTitle(),
                request.getDescription(),
                request.getXpTarget()
        );
        return ResponseEntity.ok(goal);
    }

    /**
     * Получить все цели пользователя
     * GET /api/goals/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalDTO>> getUserGoals(@PathVariable Long userId) {
        List<GoalDTO> goals = goalService.getUserGoals(userId);
        return ResponseEntity.ok(goals);
    }

    /**
     * Обновить цель
     * PUT /api/goals/{goalId}
     */
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDTO> updateGoal(
            @PathVariable Long goalId,
            @RequestBody UpdateGoalRequest request) {
        GoalDTO goal = goalService.updateGoal(goalId, request);
        return ResponseEntity.ok(goal);
    }

    /**
     * Отметить цель как завершённую
     * POST /api/goals/{goalId}/complete
     */
    @PostMapping("/{goalId}/complete")
    public ResponseEntity<GoalDTO> completeGoal(@PathVariable Long goalId) {
        GoalDTO goal = goalService.completeGoal(goalId);
        return ResponseEntity.ok(goal);
    }

    public static class CreateGoalRequest {
        private Long userId;
        private String title;
        private String description;
        private Integer xpTarget;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getXpTarget() {
            return xpTarget;
        }

        public void setXpTarget(Integer xpTarget) {
            this.xpTarget = xpTarget;
        }
    }

    public static class UpdateGoalRequest {
        private String title;
        private String description;
        private Integer currentXP;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getCurrentXP() {
            return currentXP;
        }

        public void setCurrentXP(Integer currentXP) {
            this.currentXP = currentXP;
        }
    }

}
