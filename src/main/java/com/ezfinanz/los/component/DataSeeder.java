package com.ezfinanz.los.component;

import com.ezfinanz.los.model.LoanApplication;
import com.ezfinanz.los.model.Role;
import com.ezfinanz.los.model.User;
import com.ezfinanz.los.repository.LoanApplicationRepository;
import com.ezfinanz.los.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LoanApplicationRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if the database is empty
        if (userRepository.count() == 0) {

            // 1. Create an Admin user
            User admin = User.builder()
                    .fullName("System Admin")
                    .email("admin@ezfinanz.com")
                    .phone("9999999999")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ROLE_ADMIN)
                    .isEmailVerified(true)
                    .isKycVerified(true)
                    .build();
            userRepository.save(admin);

            // 2. Create some sample customers
            User alice = User.builder()
                    .fullName("Alice Smith")
                    .email("alice@example.com")
                    .phone("8888888888")
                    .password(passwordEncoder.encode("Customer@123"))
                    .role(Role.ROLE_CUSTOMER)
                    .build();
            userRepository.save(alice);

            User bob = User.builder()
                    .fullName("Robert Johnson")
                    .email("bob@example.com")
                    .phone("7777777777")
                    .password(passwordEncoder.encode("Customer@123"))
                    .role(Role.ROLE_CUSTOMER)
                    .build();
            userRepository.save(bob);

            // 3. Generate some fake Loan Applications
            LoanApplication loan1 = LoanApplication.builder()
                    .applicant(alice)
                    .requestedAmount(new BigDecimal("50000.00"))
                    .termMonths(24)
                    .purpose("Home Renovation")
                    .status("PENDING_KYC")
                    .build();

            LoanApplication loan2 = LoanApplication.builder()
                    .applicant(bob)
                    .requestedAmount(new BigDecimal("120000.00"))
                    .termMonths(48)
                    .purpose("Used Car Purchase")
                    .status("IN_REVIEW")
                    .isKycSubmitted(true)
                    .build();

            LoanApplication loan3 = LoanApplication.builder()
                    .applicant(alice)
                    .requestedAmount(new BigDecimal("15000.00"))
                    .termMonths(12)
                    .purpose("Medical Emergency")
                    .status("APPROVED")
                    .isKycSubmitted(true)
                    .approvedAmount(new BigDecimal("15000.00"))
                    .build();

            loanRepository.saveAll(List.of(loan1, loan2, loan3));

            System.out.println("🌱 DATABASE SEEDED: 3 Users and 3 Loan Applications injected!");
        } else {
            System.out.println("✅ DATABASE ALREADY HAS DATA: Skipping Seeder.");
        }
    }
}
