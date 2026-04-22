package com.ramis.progresstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(name = "attempts")
    private Integer attempts = 0;

    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;

    @Column(name = "level_completed")
    private Integer levelCompleted = 0; // 0 = not answered, 1 = level 1, 2 = level 2, 3 = level 3

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    // текст ответа пользователя
    @Column(name = "user_answer_text", columnDefinition = "TEXT")
    private String userAnswerText;

    // оценка 0–100
    @Column(name = "ai_score")
    private Integer aiScore;

    // фидбек по ответу
    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    // Computed methods
    public int getXPGained() {
        if (levelCompleted == 0) return 0;
        return question.getXpValue() * levelCompleted / 3;
    }

    public double getAccuracy() {
        if (attempts == 0) return 0;
        return (isCorrect ? 1.0 : 0.0) * 100;
    }

}
