package com.example.userservice.repository;

import com.example.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
