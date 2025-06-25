package com.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenValidationRequest(
    @NotBlank(message = "Token is required")
    String token
) {}
