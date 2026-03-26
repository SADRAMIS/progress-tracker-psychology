package com.ramis.progresstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalDTO {
    private Long id;
    private String title;
    private String description;
    private Integer xpTarget;
    private Integer currentXP;
    private String status;  // "ACTIVE", "COMPLETED"
    private Double progressPercentage;  // 0.0 - 100.0
    private String deadline;  // ISO date
}
