package com.ramis.progresstracker.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResultDTO {
    private Long questionId;
    private Long userId;
    private int score;
    private boolean correct;
    private String feedback;
}
