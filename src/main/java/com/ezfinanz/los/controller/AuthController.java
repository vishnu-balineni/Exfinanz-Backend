package com.ezfinanz.los.controller;

import com.ezfinanz.los.dto.LoginRequest;
import com.ezfinanz.los.dto.RegisterRequest;
import com.ezfinanz.los.model.User;
import com.ezfinanz.los.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User savedUser = authService.registerUser(request);

            // Never return the password in the response! Build a clean Map.
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole());
            response.put("fullName", savedUser.getFullName());
            response.put("isKycVerified", savedUser.isKycVerified());
            response.put("phone", savedUser.getPhone());
            response.put("createdAt", savedUser.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.loginUser(request);

            // In the future this will generate and return a JWT Token.
            // For now, just return user info.
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("fullName", user.getFullName());
            response.put("isKycVerified", user.isKycVerified());
            response.put("phone", user.getPhone());
            response.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
