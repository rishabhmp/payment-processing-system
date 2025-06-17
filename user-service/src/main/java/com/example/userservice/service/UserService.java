package com.example.userservice.service;

import com.example.userservice.dto.*;

import java.util.UUID;

public interface UserService {
    UUID createUser(CreateUserRequest request);
    UserResponse getUserById(UUID id, String requesterEmail, boolean isAdmin);
    UserResponse updateUser(UUID id, UpdateUserRequest request, String requesterEmail, boolean isAdmin);
    void deleteUser(UUID id);
    void requestPasswordReset(PasswordResetRequest request);
    void performPasswordReset(PasswordResetTokenRequest request);
}
