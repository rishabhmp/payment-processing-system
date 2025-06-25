package com.example.authservice.dto;

public record AuthResponse(
    String message,
    String token
) {}
