package com.example.authservice.dto;

import java.util.UUID;

public record TokenValidationResponse(
    boolean valid,
    String email,
    String role,
    UUID userId
) {}
