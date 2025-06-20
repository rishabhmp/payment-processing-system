package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

  @PostMapping
public ResponseEntity<MessageResponse> createUser(
        @Valid @RequestBody CreateUserRequest request
) {
    log.info("Creating user: {}", request.email());
    UserResponse created = userService.createUser(request);
    URI location = URI.create("/v1/users/" + created.id());
    return ResponseEntity
            .created(location)
            .body(new MessageResponse("User created successfully"));
}

 


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        String requesterEmail = emailHeader != null ? emailHeader : "anonymous@example.com";
        log.info("Fetching user {} by requester {}", id, requesterEmail);
        UserResponse user = userService.getUserById(id, requesterEmail);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{id}")
public ResponseEntity<UserResponse> updateUser(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserRequest request,
        @RequestHeader("X-User-Email") String requesterEmail
) {
    log.info("Updating user {} by {}", id, requesterEmail);
    UserResponse updated = userService.updateUser(id, request, requesterEmail);
    return ResponseEntity.ok(updated);
}


@DeleteMapping("/{id}")
public ResponseEntity<MessageResponse> deleteUser(
        @PathVariable UUID id,
        @RequestHeader("X-User-Email") String requesterEmail
) {
    log.info("Deleting user {}", id);
    userService.deleteUser(id, requesterEmail);
    return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
}


    @PostMapping("/password-reset/request")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        log.info("Password reset requested for {}", request.email());
        userService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> performReset(@Valid @RequestBody PasswordResetTokenRequest request) {
        userService.performPasswordReset(request);
        return ResponseEntity.ok(new MessageResponse("Password has been reset."));
    }

    public record MessageResponse(String message) {}
}
