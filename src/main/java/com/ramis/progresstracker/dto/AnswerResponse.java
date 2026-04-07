package com.ramis.progresstracker.dto;

import com.ramis.progresstracker.entity.Answer;

public record AnswerResponse(
        int xpGained,
        Answer answer
) {}