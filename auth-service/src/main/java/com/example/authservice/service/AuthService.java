package com.example.authservice.service;

import com.example.authservice.dto.AuthRegistrationRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
// import com.example.authservice.dto.RegisterRequest;

public interface AuthService {
    // AuthResponse register(RegisterRequest request);
    void internalRegister(AuthRegistrationRequest request);
    AuthResponse login(LoginRequest request);
    boolean validateToken(String token);
}
