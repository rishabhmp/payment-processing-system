package com.example.userservice.service.impl;

import com.example.userservice.dto.*;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID userId = UUID.randomUUID();
    private UserProfile user;

    @BeforeEach
    void setUp() {
        user = UserProfile.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .build();
    }

    @Test
    void createUser_Success() {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "password123", "John", "Doe", "1234567890");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByPhone(request.phone())).thenReturn(false);
        when(userRepository.save(any(UserProfile.class))).thenReturn(user);

        UUID createdId = userService.createUser(request);

        assertEquals(userId, createdId);
        verify(userRepository).save(any(UserProfile.class));
        verify(restTemplate).postForEntity(eq("http://localhost:8082/internal/auth/register"), any(), eq(Void.class));
    }

    @Test
    void createUser_EmailConflict_ThrowsConflictException() {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "password123", "John", "Doe", "1234567890");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_PhoneConflict_ThrowsConflictException() {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "password123", "John", "Doe", "1234567890");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByPhone(request.phone())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(userId);

        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getFirstName(), response.firstName());
        assertEquals(user.getLastName(), response.lastName());
        assertEquals(user.getPhone(), response.phone());
    }

    @Test
    void getUserById_NotFound_ThrowsNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest("Jane", "Smith");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserProfile.class))).thenReturn(user);

        UserResponse response = userService.updateUser(userId, request);

        assertEquals("Jane", response.firstName());
        assertEquals("Smith", response.lastName());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getPhone(), response.phone());
    }

    @Test
    void updateUser_NotFound_ThrowsNotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest("Jane", "Smith");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_NotFound_ThrowsNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
    }
}