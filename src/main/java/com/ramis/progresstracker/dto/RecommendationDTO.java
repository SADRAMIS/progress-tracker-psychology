package com.ramis.progresstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private Long id;
    private String type; // MOTIVATION, FOCUS, CONSISTENCY, LEARNING_PATH
    private String content;
    private String priority; // LOW, MEDIUM, HIGH
    private boolean read;
    private String actionItems; // Конкретные действия
}
