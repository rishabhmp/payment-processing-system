package com.example.authservice.service;

import com.example.authservice.dto.AuthRegistrationRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;

public interface AuthService {
    void internalRegister(AuthRegistrationRequest request);
    AuthResponse login(LoginRequest request);
    boolean validateToken(String token);
}
