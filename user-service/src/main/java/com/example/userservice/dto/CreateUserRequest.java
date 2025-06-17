package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(

    @Email(message = "invalid email format")
    @NotBlank(message = "email is required")
    String email,

    // @NotBlank(message = "password is required")
    // String password,

    @NotBlank(message = "full name is required")
    String fullName,

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "invalid phone number")
    String phone

) {}
