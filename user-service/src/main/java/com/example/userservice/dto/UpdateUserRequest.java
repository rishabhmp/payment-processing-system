package com.example.userservice.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 100, message = "First name must be under 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must be under 100 characters")
        String lastName,

        @Pattern(regexp = "^\\d{10}$", message = "Phone must be exactly 10 digits")
        String phone
) {}