package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

@PostMapping
public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody CreateUserRequest request) {
    log.info("Creating user: {}", request.email());
    UUID userId = userService.createUser(request);

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-User-Id", userId.toString());

    Map<String, String> response = new HashMap<>();
    response.put("message", "User created successfully");

    return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(response); 
}

   @GetMapping("/{id}")
public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
    log.info("Fetching profile for user ID: {}", id);
    return ResponseEntity.ok(userService.getUserById(id));
}

  @PutMapping("/{id}")
public ResponseEntity<UserResponse> updateUser(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserRequest request
) {
    log.info("Updating user with ID: {}", id);
    return ResponseEntity.ok(userService.updateUser(id, request));
}


    @DeleteMapping("/{id}")
public ResponseEntity<MessageResponse> deleteUser(@PathVariable UUID id) {
    log.info("Deleting user by ID: {}", id);
    userService.deleteUser(id);
    return ResponseEntity.ok(new MessageResponse("User deleted successfully"));  
}

    public record MessageResponse(String message) {}
}
