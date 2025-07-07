package com.example.authservice.service.impl;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.entity.UserCredential;
import com.example.authservice.exception.UnauthorizedException;
import com.example.authservice.repository.UserCredentialRepository;
import com.example.authservice.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String email = "test@example.com";
    private final String password = "password123";

    @Test
    void login_Success_ReturnsToken() {
        LoginRequest request = new LoginRequest(email, password);
        UserCredential user = UserCredential.builder()
                .email(email)
                .passwordHash("hashedPass")
                .role("ROLE_USER")
                .build();

        when(userCredentialRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashedPass")).thenReturn(true);
        when(jwtTokenUtil.generateToken(email, "ROLE_USER")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("Login successful", response.message());
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_UserNotFound_ThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest(email, password);

        when(userCredentialRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void login_InvalidPassword_ThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest(email, password);
        UserCredential user = UserCredential.builder()
                .email(email)
                .passwordHash("wrongHash")
                .role("ROLE_USER")
                .build();

        when(userCredentialRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "wrongHash")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void validateToken_ReturnsTrue() {
        String token = "valid.jwt.token";
        when(jwtTokenUtil.validateToken(token)).thenReturn(true);

        assertTrue(authService.validateToken(token));
    }

    @Test
    void validateToken_ReturnsFalse() {
        String token = "invalid.jwt.token";
        when(jwtTokenUtil.validateToken(token)).thenReturn(false);

        assertFalse(authService.validateToken(token));
    }
}