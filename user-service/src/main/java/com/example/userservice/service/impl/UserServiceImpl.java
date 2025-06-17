package com.example.userservice.service.impl;

import com.example.userservice.dto.*;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.exception.*;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.reset-token.secret:resetSecretKey12345678901234567890}")
    private String resetTokenSecret;

    @Value("${app.reset-token.expiry-minutes:30}")
    private long resetTokenExpiryMinutes;

    @Value("${app.audit.url:http://audit-service:8086/v1/audit/events}")
    private String auditServiceUrl;

   @Override
@Transactional
public UUID createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
        throw new ConflictException("Email already in use");
    }

    UserProfile profile = UserProfile.builder()
            .email(request.email())
            .fullName(request.fullName())
            .phone(request.phone())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    userRepository.save(profile);
    sendAudit("CREATE_USER", profile.getEmail(), profile.getId());
    return profile.getId();
}


    @Override
    public UserResponse getUserById(UUID id, String requesterEmail, boolean isAdmin) {
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getEmail().equals(requesterEmail) && !isAdmin) {
            throw new ForbiddenException("Access denied");
        }

        return new UserResponse(user.getEmail(), user.getFullName(), user.getPhone());
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request, String requesterEmail, boolean isAdmin) {
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getEmail().equals(requesterEmail) && !isAdmin) {
            throw new ForbiddenException("editing other user as non-admin");
        }

        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.phone() != null) user.setPhone(request.phone());

        return new UserResponse(user.getEmail(), user.getFullName(), user.getPhone());
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void requestPasswordReset(PasswordResetRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            String token = Jwts.builder()
                    .setSubject(user.getEmail())
                    .setExpiration(Date.from(Instant.now().plusSeconds(resetTokenExpiryMinutes * 60)))
                    .signWith(getSigningKey())
                    .compact();

                    System.out.println("RESET TOKEN: " + token);
            
            // Normally send via email — for now, print that in terminal
            sendAudit("PASSWORD_RESET_REQUEST", user.getEmail(), user.getId());
        });
    }

    @Override
    public void performPasswordReset(PasswordResetTokenRequest request) {
        String email;
        try {
            email = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(request.token())
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            throw new BadRequestException("Token invalid or expired");
        }

        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            // In system, password update happens in auth-service
            sendAudit("PASSWORD_RESET_COMPLETE", user.getEmail(), user.getId());
        }, () -> {
            throw new NotFoundException("User not found");
        });
    }

    private void sendAudit(String action, String email, UUID userId) {
        var event = new AuditEventRequest(
                "user-service", action,
                new AuditPayload(email, userId),
                Instant.now().toString()
        );
        try {
            restTemplate.postForEntity(auditServiceUrl, event, Void.class);
        } catch (Exception e) {
            // log failure, do not break user flow
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(resetTokenSecret.getBytes());
    }

    // Inner static classes for audit request payload
    record AuditEventRequest(String service, String action, AuditPayload payload, String timestamp) {}
    record AuditPayload(String email, UUID userId) {}
}
