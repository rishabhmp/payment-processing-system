package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        String lastName,

        @NotBlank(message = "Email is required")
        @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format"
        )
        String email,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits and contain only numbers")

        String phone
) {}
