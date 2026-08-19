package com.ezfinanz.los.service;

import com.ezfinanz.los.dto.LoginRequest;
import com.ezfinanz.los.dto.RegisterRequest;
import com.ezfinanz.los.model.Role;
import com.ezfinanz.los.model.User;
import com.ezfinanz.los.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        // Build the User object
        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                // Hash the password for security!
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_CUSTOMER) // Automatically make them a standard customer
                .isEmailVerified(false)
                .isKycVerified(false)
                .build();

        return userRepository.save(newUser);
    }

    public User loginUser(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Compare the raw password from React against the hashed password in DB
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}
