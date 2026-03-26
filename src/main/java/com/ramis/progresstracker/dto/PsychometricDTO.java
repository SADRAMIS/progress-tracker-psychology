package com.ramis.progresstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsychometricDTO {
    private Long userId;
    private Double motivation;
    private Double confidence;
    private Double resilience;
    private Double focus;
    private Double consistency;
    private Double overallScore;
    private String analysis; // Текстовый анализ
    private String recommendation; // Рекомендация
}
