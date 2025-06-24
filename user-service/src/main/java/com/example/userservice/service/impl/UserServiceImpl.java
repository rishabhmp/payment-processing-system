package com.example.userservice.service.impl;

import com.example.userservice.dto.*;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UUID createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new ConflictException("Phone number already exists");
        }

        UserProfile user = UserProfile.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .build();

        UserProfile savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
public UserResponse getUserById(UUID id) {
    UserProfile user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    return new UserResponse(user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhone());
}

   @Override
@Transactional
public UserResponse updateUser(UUID id, UpdateUserRequest request) {
    UserProfile user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));

    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());

    // Update phone only if it's different
    // String newPhone = request.phone();
    // if (!user.getPhone().equals(newPhone)) {
    //     if (userRepository.existsByPhone(newPhone)) {
    //         throw new ConflictException("Phone number already exists");
    //     }
    //     user.setPhone(newPhone);
    // }

    user.setUpdatedAt(Instant.now());
    userRepository.save(user);

    return new UserResponse(
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone()
    );
}

    @Override
@Transactional
public void deleteUser(UUID id) {
    UserProfile user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found"));
    userRepository.delete(user);
}

    // @Override
    // public void requestPasswordReset(PasswordResetRequest request) {
    //     log.info("Password reset request received for {}", request.email());
    // }

    // @Override
    // public void performPasswordReset(PasswordResetTokenRequest request) {
    //     log.info("Password reset token submitted: {}", request.token());
    // }
}
