package com.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetTokenRequest(
        @NotBlank(message = "token is required")
        String token,

        @NotBlank(message = "newPassword is required")
        String newPassword
) {}
