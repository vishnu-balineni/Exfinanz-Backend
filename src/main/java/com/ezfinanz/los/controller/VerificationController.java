package com.ezfinanz.los.controller;

import com.ezfinanz.los.model.BankDetails;
import com.ezfinanz.los.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/{userId}/kyc")
    public ResponseEntity<?> submitKyc(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            verificationService.submitKycDocument(userId, request.get("documentType"), request.get("documentUrl"));
            return ResponseEntity.ok(Map.of("message", "KYC Document saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/bank")
    public ResponseEntity<?> submitBankDetails(@PathVariable Long userId, @RequestBody BankDetails request) {
        try {
            verificationService.submitBankDetails(userId, request);
            return ResponseEntity.ok(Map.of("message", "Bank details secured and verified"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{userId}/finalize")
    public ResponseEntity<?> finalizeVerification(@PathVariable Long userId) {
        try {
            verificationService.finalizeVerification(userId);
            return ResponseEntity.ok(Map.of("message", "User verification successfully completed!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<?> getVerificationStatus(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(Map.of(
                    "kycDocuments", verificationService.getUserDocuments(userId),
                    "bankDetails", verificationService.getUserBankDetails(userId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
