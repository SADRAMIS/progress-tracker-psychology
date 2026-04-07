package com.ramis.progresstracker.dto;

public record WeeklyStats(
        int weeklyXp,
        int questionsSolved,
        int totalXp
) {}