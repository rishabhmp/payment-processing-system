package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.service.PaymentService;
import com.example.paymentservice.exception.PaymentException;
import com.example.paymentservice.security.JwtTokenValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final JwtTokenValidator jwtTokenValidator;


    @PostMapping("/payment-intent")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequest request, @RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Missing token");
        }
        if (!jwtTokenValidator.isValidToken(authHeader.substring(7))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }

        logger.info("Processing payment for {}", request.getCustomerEmail());
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
}
