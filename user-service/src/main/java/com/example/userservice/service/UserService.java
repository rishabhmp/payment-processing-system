package com.example.userservice.service;

import com.example.userservice.dto.*;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(UUID id, String requesterEmail);
    UserResponse updateUser(UUID id, UpdateUserRequest request, String requesterEmail);
    void deleteUser(UUID id, String requesterEmail);
    void requestPasswordReset(PasswordResetRequest request);
    void performPasswordReset(PasswordResetTokenRequest request); // corrected name
}
