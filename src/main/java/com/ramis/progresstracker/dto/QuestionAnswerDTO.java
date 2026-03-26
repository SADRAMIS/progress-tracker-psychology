package com.ramis.progresstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDTO {
    private Long questionId;
    private Integer questionNumber;
    private String title;
    private String category;
    private Integer difficulty;
    private Integer levelCompleted; // 0, 1, 2, or 3
    private Integer attempts;
    private Integer timeSpentMinutes;
    private Double xpGained;
    private String notes;

}
