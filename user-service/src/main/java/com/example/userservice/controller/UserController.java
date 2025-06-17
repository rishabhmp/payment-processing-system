package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequest request) {
        UUID userId = userService.createUser(request);
        return ResponseEntity.created(URI.create("/v1/users/" + userId)).build();
    }

    @GetMapping("/{id}")
public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id,
                                                Principal principal,
                                                @RequestHeader(value = "X-User-Role", required = false) String role,
                                                @RequestHeader(value = "X-User-Email", required = false) String emailHeader) {
    boolean isAdmin = "ROLE_ADMIN".equals(role);
    String requesterEmail = (principal != null) ? principal.getName() :
                            (emailHeader != null ? emailHeader : "g0@example.com");

    System.out.println("Requester Email: " + requesterEmail);
    UserResponse user = userService.getUserById(id, requesterEmail, isAdmin);
    return ResponseEntity.ok(user);
}

   @PutMapping("/{id}")
public ResponseEntity<UserResponse> updateUser(
        @PathVariable UUID id,
        @RequestBody UpdateUserRequest request,
        @RequestHeader("X-User-Email") String email,
        @RequestHeader("X-User-Role") String role
) {
    boolean isAdmin = role.equalsIgnoreCase("ROLE_ADMIN");
    UserResponse updated = userService.updateUser(id, request, email, isAdmin);
    return ResponseEntity.ok(updated);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset/request")
public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequest request) {
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
