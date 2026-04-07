package com.ramis.progresstracker.dto;

public record CreateUserRequest(
        String email,
        String name
) {}