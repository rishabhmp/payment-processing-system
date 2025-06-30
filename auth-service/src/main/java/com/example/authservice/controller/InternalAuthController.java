package com.example.authservice.controller;

import com.example.authservice.dto.AuthRegistrationRequest;
import com.example.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
@Slf4j
public class InternalAuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> internalRegister(@RequestBody AuthRegistrationRequest request) {
        log.info("Received internal registration request for: {}", request.email());
        authService.internalRegister(request);
        return ResponseEntity.ok().build();
    }
}
