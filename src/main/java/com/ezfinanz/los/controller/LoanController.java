package com.ezfinanz.los.controller;

import com.ezfinanz.los.dto.LoanApplyRequest;
import com.ezfinanz.los.model.LoanApplication;
import com.ezfinanz.los.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForLoan(@Valid @RequestBody LoanApplyRequest request) {
        try {
            LoanApplication application = loanService.applyForLoan(request);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/eligibility")
    public ResponseEntity<?> checkEligibility(@RequestBody Map<String, Object> request) {
        try {
            BigDecimal income = new BigDecimal(request.getOrDefault("income", "0").toString());
            BigDecimal debts = new BigDecimal(request.getOrDefault("debts", "0").toString());
            int cibil = Integer.parseInt(request.getOrDefault("cibil", "0").toString());

            return ResponseEntity.ok(loanService.checkEligibility(income, debts, cibil));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-loans/{userId}")
    public ResponseEntity<?> getMyLoans(@PathVariable Long userId) {
        try {
            List<LoanApplication> loans = loanService.getMyLoans(userId);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingApplications() {
        try {
            List<LoanApplication> pendingLoans = loanService.getAllPendingLoans();
            return ResponseEntity.ok(pendingLoans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllApplications() {
        try {
            List<LoanApplication> allLoans = loanService.getAllLoans();
            return ResponseEntity.ok(allLoans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/{loanId}/review")
    public ResponseEntity<?> reviewApplication(
            @PathVariable Long loanId,
            @RequestBody Map<String, Object> reviewData) {
        try {
            String newStatus = (String) reviewData.get("status");
            String adminNotes = (String) reviewData.get("adminNotes");
            BigDecimal approvedAmount = null;

            if (reviewData.containsKey("approvedAmount") && reviewData.get("approvedAmount") != null) {
                approvedAmount = new BigDecimal(reviewData.get("approvedAmount").toString());
            }

            LoanApplication application = loanService.reviewApplication(loanId, newStatus, approvedAmount, adminNotes);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{loanId}/pay")
    public ResponseEntity<?> payEmi(@PathVariable Long loanId) {
        try {
            Map<String, Object> result = loanService.payNextEmi(loanId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
