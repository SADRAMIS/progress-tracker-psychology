package com.ramis.progresstracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category; // BASIC_JAVA, SPRING_BOOT, PATTERNS, SQL, etc.

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Column(name = "xp_value", nullable = false)
    private Integer xpValue;

    @Column(name = "theory_content", columnDefinition = "LONGTEXT")
    private String theoryContent;

    @Column(name = "code_example", columnDefinition = "LONGTEXT")
    private String codeExample;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
