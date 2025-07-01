package com.example.paymentservice.security;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class JwtTokenValidator {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isValidToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                "http://localhost:8082/v1/auth/validate",  // points to auth-service
                HttpMethod.GET,
                entity,
                Map.class
            );
            return (Boolean) response.getBody().get("valid");
        } catch (Exception e) {
            return false;
        }
    }
}
