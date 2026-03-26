package com.ramis.progresstracker.repository;

import com.ramis.progresstracker.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    // Базовые запросы по пользователю (для GoalController /apigoals/user/{userId})
    List<Goal> findByUserIdOrderByCreatedDateDesc(Long userId);

    // Активные цели пользователя (status=ACTIVE)
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = :status ORDER BY g.createdDate DESC")
    List<Goal> findActiveGoalsByUserId(@Param("userId") Long userId, @Param("status") Goal.GoalStatus status);

    // Завершенные цели с прогрессом >80%
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.status = :status AND g.progressPercentage > 0.8 ORDER BY g.createdDate DESC")
    List<Goal> findCompletedHighProgressGoalsByUserId(@Param("userId") Long userId, @Param("status") Goal.GoalStatus status);

    // Цели по дедлайну (для уведомлений/аналитики)
    List<Goal> findByUserIdAndDeadlineBeforeOrderByDeadlineAsc(Long userId, LocalDate deadline);

    // Статистика: кол-во активных целей по userId
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user.id = :userId AND g.status = :status")
    Long countActiveGoalsByUserId(@Param("userId") Long userId, @Param("status") Goal.GoalStatus status);

    // Цели близкие к завершению (currentXP >= 90% xpTarget)
    @Query("SELECT g FROM Goal g WHERE g.user.id = :userId AND g.currentXP >= 0.9 * g.xpTarget AND g.status = 'ACTIVE' ORDER BY (g.currentXP * 1.0 / g.xpTarget) DESC")
    List<Goal> findNearCompletionGoalsByUserId(@Param("userId") Long userId);

}
