package com.example.userservice.dto;

public record AuthRegistrationRequest(
    String email,
    String password
) {}
