package com.example.userservice.dto;

import java.util.UUID;

public record UserResponse(
         UUID id,
        String email,
        String firstName,
        String lastName,
        String phone
) {}