package com.ramis.progresstracker.dto;

public record SubmitAnswerRequest(
        Long userId,
        Long questionId,
        boolean isCorrect
) {}