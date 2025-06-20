package com.example.userservice.dto;

public record UserResponse(
        String email,
        String firstName,
        String lastName,
        String phone
) {}
