package com.example.authservice.service;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    boolean validateToken(String token);
}
