// path: src/main/java/com/example/authservice/controller/AuthController.java
package com.example.authservice.controller;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
// import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.exception.UnauthorizedException;
import com.example.authservice.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
// import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // @PostMapping("/register")
    // public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    //     log.info("Registering user with email: {}", request.email());
    //     AuthResponse response = authService.register(request);
    //     return ResponseEntity.status(201).body(response);
    // }

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

    return ResponseEntity.ok(Map.of("valid", valid));
}

}
