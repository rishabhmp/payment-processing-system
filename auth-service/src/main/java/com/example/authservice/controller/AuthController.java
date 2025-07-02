// path: src/main/java/com/example/authservice/controller/AuthController.java
package com.example.authservice.controller;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.exception.UnauthorizedException;
import com.example.authservice.service.AuthService;
import jakarta.validation.Valid;
import com.example.authservice.security.JwtTokenUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Attempting login for user: {}", request.email());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Object> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Validating token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        boolean valid = authService.validateToken(token);

        if (valid) {
            String email = jwtTokenUtil.getEmailFromToken(token);  // Extract email (sub claim)
            return ResponseEntity.ok(Map.of("valid", valid, "email", email));  // Return the email as "sub"
        } else {
            return ResponseEntity.ok(Map.of("valid", valid));
        }
    }

}
