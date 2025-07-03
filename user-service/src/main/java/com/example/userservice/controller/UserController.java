
package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.service.UserService;
import com.example.userservice.security.JwtTokenValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtTokenValidator jwtTokenValidator;

    // Helper method to extract email from the token and validate
    private String getEmailFromTokenOrUnauthorized(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        String emailFromToken = jwtTokenValidator.extractEmail(token);
        return emailFromToken;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user: {}", request.email());
        UUID userId = userService.createUser(request);

        Map<String, String> response = Map.of(
            "message", "User created successfully",
            "userId", userId.toString()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id, @RequestHeader(name = "Authorization", required = false) String authHeader) {
        String emailFromToken = getEmailFromTokenOrUnauthorized(authHeader);

        if (emailFromToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized user"));
        }

        UserResponse user = userService.getUserById(id);

        if (!user.email().equals(emailFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Forbidden: Access denied"));
        }

        log.info("Fetching profile for user ID: {}", id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request, 
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        String emailFromToken = getEmailFromTokenOrUnauthorized(authHeader);

        if (emailFromToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized user"));
        }

        UserResponse user = userService.getUserById(id);

        if (!user.email().equals(emailFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Forbidden: You can only update your own data"));
        }

        log.info("Updating user with ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(
            @PathVariable UUID id, 
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        String emailFromToken = getEmailFromTokenOrUnauthorized(authHeader);

        if (emailFromToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized user"));
        }

        UserResponse user = userService.getUserById(id);

        if (!user.email().equals(emailFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Forbidden: You can only delete your own data"));
        }

        log.info("Deleting user by ID: {}", id);
        userService.deleteUser(id);

        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    public record MessageResponse(String message) {}
}


