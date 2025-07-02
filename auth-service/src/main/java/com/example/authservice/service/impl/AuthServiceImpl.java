package com.example.authservice.service.impl;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.entity.UserCredential;
import com.example.authservice.exception.UnauthorizedException;
import com.example.authservice.repository.UserCredentialRepository;
import com.example.authservice.security.JwtTokenUtil;
import com.example.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.authservice.dto.AuthRegistrationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void internalRegister(AuthRegistrationRequest request) {
        log.info("Registering internal user: {}", request.email());

        if (userCredentialRepository.existsByEmail(request.email())) {
            log.warn("User already exists in auth DB: {}", request.email());
            return;
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        UserCredential user = UserCredential.builder()
                .email(request.email())
                .passwordHash(encodedPassword)
                .role("ROLE_USER")
                .build();

        userCredentialRepository.save(user);
        log.info("User saved in auth DB: {}", user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.email());

        UserCredential user = userCredentialRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed — user not found: {}", request.email());
                    return new UnauthorizedException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Login failed — invalid password for user: {}", user.getEmail());
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRole());
        log.info("Login successful for user: {}", user.getEmail());

        return new AuthResponse("Login successful", token);
    }

    @Override
    public boolean validateToken(String token) {
        boolean valid = jwtTokenUtil.validateToken(token);
        log.debug("Token validation result: {}", valid);
        return valid;
    }
}
