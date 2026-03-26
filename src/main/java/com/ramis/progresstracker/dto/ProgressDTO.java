package com.ramis.progresstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {
    private LocalDate date;
    private Integer xpGained;
    private Double hoursStudied;
    private Integer questionsSolved;
    private Double percentageCorrect;
    private Integer streakDays;
    private Integer moodRating; // 1-5
    private String summary;
}
