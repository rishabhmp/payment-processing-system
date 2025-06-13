package com.example.userservice.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String fullName;
    private String phone;
}
