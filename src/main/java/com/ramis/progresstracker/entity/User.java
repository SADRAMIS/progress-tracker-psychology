package com.ramis.progresstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "level")
    private Integer level = 1;

    @Column(name = "xp_total")
    private Integer totalXP = 0;

    @Column(name = "motivation_score", columnDefinition = "DECIMAL(5,2)")
    private Double motivationScore = 50.0;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    // Relations
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PsychometricScore> psychometricScores = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgressRecord> progressRecords = new ArrayList<>();

    // Getters for computed properties
    public int calculateLevel() {
        return (totalXP / 1000) + 1;
    }

    public int getXPToNextLevel() {
        int currentLevelXP = (level - 1) * 1000;
        int nextLevelXP = level * 1000;
        return nextLevelXP - currentLevelXP;
    }

    public double getProgressToNextLevel() {
        int currentLevelXP = (level - 1) * 1000;
        int xpInCurrentLevel = totalXP - currentLevelXP;
        return (xpInCurrentLevel * 100.0) / getXPToNextLevel();
    }

}
