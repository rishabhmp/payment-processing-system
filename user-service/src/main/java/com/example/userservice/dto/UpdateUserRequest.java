package com.example.userservice.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 255)
        String fullName,

        @Size(max = 20)
        String phone
) {}
