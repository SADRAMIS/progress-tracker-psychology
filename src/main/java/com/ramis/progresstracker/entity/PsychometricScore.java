package com.ramis.progresstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "psychometric_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsychometricScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Психологические метрики (0-100 шкала)
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double motivation; // Мотивация (энтузиазм)

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double confidence; // Уверенность в своих знаниях

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double resilience; // Устойчивость к ошибкам

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double focus; // Концентрация внимания

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double consistency; // Постоянство в учёбе

    @Column(name = "overall_score", columnDefinition = "DECIMAL(5,2)")
    private Double overallScore;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    // Computed method
    public void calculateOverallScore() {
        if (motivation != null && confidence != null && resilience != null && focus != null && consistency != null) {
            this.overallScore = (motivation + confidence + resilience + focus + consistency) / 5.0;
        }
    }
}
