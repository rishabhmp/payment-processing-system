package com.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        String lastName

) {}