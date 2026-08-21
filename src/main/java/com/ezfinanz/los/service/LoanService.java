package com.ezfinanz.los.service;

import com.ezfinanz.los.dto.LoanApplyRequest;
import com.ezfinanz.los.model.LoanApplication;
import com.ezfinanz.los.model.User;
import com.ezfinanz.los.model.LoanRepaymentSchedule;
import com.ezfinanz.los.repository.LoanRepaymentScheduleRepository;
import com.ezfinanz.los.repository.LoanApplicationRepository;
import com.ezfinanz.los.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanApplicationRepository loanRepository;
    private final UserRepository userRepository;
    private final LoanRepaymentScheduleRepository emiRepository;

    public LoanApplication applyForLoan(LoanApplyRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isKycVerified()) {
            throw new RuntimeException("User must be mathematically and legally verified before applying.");
        }

        LoanApplication application = LoanApplication.builder()
                .applicant(user)
                .requestedAmount(request.getRequestedAmount())
                .termMonths(request.getTermMonths())
                .purpose(request.getPurpose())
                .isUrgent(request.getIsUrgent() != null ? request.getIsUrgent() : false)
                .status("PENDING_ADMIN_REVIEW") // Jump instantly to Admin review
                .isKycSubmitted(true) // Deprecated flag, kept for backward compatibility if needed
                .build();

        return loanRepository.save(application);
    }

    public List<LoanApplication> getMyLoans(Long userId) {
        return loanRepository.findByApplicantId(userId);
    }

    public List<LoanApplication> getAllPendingLoans() {
        return loanRepository.findByStatus("PENDING_ADMIN_REVIEW");
    }

    public List<LoanApplication> getAllLoans() {
        return loanRepository.findAll();
    }

    public LoanApplication reviewApplication(Long loanId, String newStatus, BigDecimal approvedAmount,
            String adminNotes) {
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan Application not found"));

        application.setStatus(newStatus);
        application.setAdminNotes(adminNotes);

        if ("APPROVED".equals(newStatus)) {
            BigDecimal finalAmount = approvedAmount != null ? approvedAmount : application.getRequestedAmount();
            application.setApprovedAmount(finalAmount);

            // Build the Automated EMI Schedule
            int months = application.getTermMonths();
            if (months <= 0)
                months = 12; // safety fallback

            // Simple EMI flat interest calculation (e.g., 12% flat rate / 1%) for demo
            // purposes
            BigDecimal interestFactor = new BigDecimal("1.12");
            BigDecimal totalRepayable = finalAmount.multiply(interestFactor);
            BigDecimal monthlyEmi = totalRepayable.divide(new BigDecimal(months), 0, RoundingMode.HALF_UP);

            for (int i = 1; i <= months; i++) {
                LoanRepaymentSchedule schedule = LoanRepaymentSchedule.builder()
                        .loanApplication(application)
                        .installmentNumber(i)
                        .dueDate(LocalDate.now().plusMonths(i))
                        .emiAmount(monthlyEmi)
                        .principalComponent(finalAmount.divide(new BigDecimal(months), 0, RoundingMode.HALF_UP))
                        .interestComponent(monthlyEmi
                                .subtract(finalAmount.divide(new BigDecimal(months), 0, RoundingMode.HALF_UP)))
                        .status("PENDING")
                        .build();
                emiRepository.save(schedule);
            }
        }

        return loanRepository.save(application);
    }

    public Map<String, Object> checkEligibility(BigDecimal income, BigDecimal currentEmis, int cibil) {
        Map<String, Object> response = new HashMap<>();

        if (income == null || income.compareTo(BigDecimal.ZERO) == 0) {
            response.put("eligible", false);
            response.put("reason", "Income must be greater than 0");
            return response;
        }

        BigDecimal dti = currentEmis.divide(income, 2, RoundingMode.HALF_UP);

        if (cibil > 700 && dti.compareTo(new BigDecimal("0.50")) < 0) {
            response.put("eligible", true);
            // Rough calculation for max loan (e.g. 50% left of income * 12 months * max 3
            // years roughly)
            BigDecimal disposable = income.subtract(currentEmis).multiply(new BigDecimal("0.5"));
            BigDecimal maxLoan = disposable.multiply(new BigDecimal("36"));
            response.put("maxAmount", maxLoan);
        } else {
            response.put("eligible", false);
            response.put("reason", "Debt to income ratio is too high, or CIBIL score is too low.");
        }

        return response;
    }

    private void generateEmiSchedule(LoanApplication application) {
        int months = application.getTermMonths();
        BigDecimal principal = application.getApprovedAmount();
        BigDecimal monthlyRate = new BigDecimal("0.01"); // Flat 1% monthly

        BigDecimal interestPart = principal.multiply(monthlyRate);
        BigDecimal principalPart = principal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
        BigDecimal emi = principalPart.add(interestPart);

        LocalDate nextDueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= months; i++) {
            LoanRepaymentSchedule schedule = LoanRepaymentSchedule.builder()
                    .loanApplication(application)
                    .installmentNumber(i)
                    .dueDate(nextDueDate)
                    .emiAmount(emi)
                    .principalComponent(principalPart)
                    .interestComponent(interestPart)
                    .status("PENDING")
                    .build();

            emiRepository.save(schedule);
            nextDueDate = nextDueDate.plusMonths(1);
        }
    }

    public Map<String, Object> payNextEmi(Long loanId) {
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        List<LoanRepaymentSchedule> schedules = emiRepository
                .findByLoanApplicationIdOrderByInstallmentNumberAsc(loanId);

        LoanRepaymentSchedule nextEmi = schedules.stream()
                .filter(s -> "PENDING".equals(s.getStatus()))
                .sorted((a, b) -> a.getDueDate().compareTo(b.getDueDate()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No pending EMI found for this loan"));

        nextEmi.setStatus("PAID");
        emiRepository.save(nextEmi);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment successful");
        response.put("paidAmount", nextEmi.getEmiAmount());
        response.put("receipt", "RCPT-" + System.currentTimeMillis());
        return response;
    }

    public Map<String, Object> forecloseLoan(Long loanId) {
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        List<LoanRepaymentSchedule> schedules = emiRepository
                .findByLoanApplicationIdOrderByInstallmentNumberAsc(loanId);

        List<LoanRepaymentSchedule> pendingSchedules = schedules.stream()
                .filter(s -> "PENDING".equals(s.getStatus()))
                .toList();

        if (pendingSchedules.isEmpty()) {
            throw new RuntimeException("No pending balance found for this loan");
        }

        BigDecimal totalPaid = BigDecimal.ZERO;
        for (LoanRepaymentSchedule schedule : pendingSchedules) {
            schedule.setStatus("PAID");
            totalPaid = totalPaid.add(schedule.getPrincipalComponent()); // Usually foreclosure waives future interest
        }
        emiRepository.saveAll(pendingSchedules);

        // Mark the loan as fully paid / closed
        application.setStatus("CLOSED");
        loanRepository.save(application);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Loan foreclosed successfully");
        response.put("paidAmount", totalPaid);
        response.put("receipt", "FCLS-" + System.currentTimeMillis());
        return response;
    }
}
