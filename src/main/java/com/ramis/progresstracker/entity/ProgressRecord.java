package com.ramis.progresstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date = LocalDate.now();

    @Column(name = "xp_gained")
    private Integer xpGained = 0;

    @Column(name = "hours_studied", columnDefinition = "DECIMAL(4,2)")
    private Double hoursStudied = 0.0;

    @Column(name = "questions_solved")
    private Integer questionsSolved = 0;

    @Column(name = "percentage_correct", columnDefinition = "DECIMAL(5,2)")
    private Double percentageCorrect = 0.0;

    @Column(name = "streak_days")
    private Integer streakDays = 0;

    @Column(name = "mood_rating")
    private Integer moodRating; // 1-5 шкала

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
}
