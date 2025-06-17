package com.example.userservice.dto;

public record UserResponse(
        String email,
        String fullName,
        String phone
) {}
