package com.ramis.progresstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "xp_target", nullable = false)
    private Integer xpTarget;

    @Column(name = "current_xp")
    private Integer currentXP = 0;

    @Enumerated(EnumType.STRING)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    // Methods
    public double getProgressPercentage() {
        return (currentXP * 100.0) / xpTarget;
    }

    public boolean isCompleted() {
        return currentXP >= xpTarget;
    }

    public enum GoalStatus {
        ACTIVE, COMPLETED, ABANDONED
    }

}
