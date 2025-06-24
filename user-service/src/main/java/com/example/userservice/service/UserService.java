package com.example.userservice.service;

import com.example.userservice.dto.*;

import java.util.UUID;

public interface UserService {
    UUID createUser(CreateUserRequest request);
    UserResponse getUserById(UUID id);
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    void deleteUser(UUID id);
    // void requestPasswordReset(PasswordResetRequest request);
    // void performPasswordReset(PasswordResetTokenRequest request);
}
