package com.ramis.progresstracker.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewAnswerDTO {
    private Long userId;
    private Long questionId;
    private String userAnswerText;
}
