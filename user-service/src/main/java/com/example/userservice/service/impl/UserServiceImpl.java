package com.example.userservice.service.impl;

import com.example.userservice.dto.*;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.exception.ForbiddenException;
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
public UserResponse createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
        throw new ConflictException("Email already exists");
    }

    if (userRepository.existsByPhone(request.phone())) {
        throw new ConflictException("Phone number already exists");
    }

    UserProfile user = UserProfile.builder()
            .id(UUID.randomUUID())
            .email(request.email())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phone(request.phone())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    userRepository.save(user);

return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhone()
);}


    @Override
    public UserResponse getUserById(UUID id, String requesterEmail) {
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

   return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhone()
); }

    @Override
@Transactional
public UserResponse updateUser(UUID id, UpdateUserRequest request, String requesterEmail) {
    UserProfile user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));

    if (!user.getEmail().equals(requesterEmail)) {
        throw new ForbiddenException("You can only update your own profile");
    }

    if (request.phone() != null && !request.phone().equals(user.getPhone())) {
        if (userRepository.existsByPhone(request.phone())) {
            throw new ConflictException("Phone number already exists");
        }
        user.setPhone(request.phone());
    }

    if (request.firstName() != null) user.setFirstName(request.firstName());
    if (request.lastName() != null) user.setLastName(request.lastName());

    user.setUpdatedAt(Instant.now());
    userRepository.save(user);

    return new UserResponse( user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhone());
}


@Override
@Transactional
public void deleteUser(UUID id, String requesterEmail) {
    UserProfile user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));

    if (!user.getEmail().equals(requesterEmail)) {
        throw new ForbiddenException("You can only delete your own profile");
    }

    userRepository.delete(user);
}


    @Override
    public void requestPasswordReset(PasswordResetRequest request) {
        log.info("Password reset request received for {}", request.email());
    }

    @Override
public void performPasswordReset(PasswordResetTokenRequest request) {
    log.info("Password reset token submitted: {}", request.token());
    
}
}
