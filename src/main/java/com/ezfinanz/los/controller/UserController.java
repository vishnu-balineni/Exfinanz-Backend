package com.ezfinanz.los.controller;

import com.ezfinanz.los.model.User;
import com.ezfinanz.los.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "fullName", user.getFullName(),
                    "email", user.getEmail(),
                    "phone", user.getPhone(),
                    "isKycVerified", user.isKycVerified(),
                    "role", user.getRole(),
                    "joinedAt", user.getCreatedAt()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/phone")
    public ResponseEntity<?> updatePhone(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String newPhone = payload.get("phone");
            if (newPhone == null || newPhone.isEmpty()) {
                throw new RuntimeException("Phone number is required");
            }
            user.setPhone(newPhone);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Phone successfully updated", "phone", user.getPhone()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
