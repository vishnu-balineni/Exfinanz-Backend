package com.ezfinanz.los.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link this loan application to the User who created it
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User applicant;

    @Column(nullable = false)
    private BigDecimal requestedAmount;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false)
    private String purpose;

    // Status: PENDING_KYC, REVIEW, APPROVED, DISBURSED, REJECTED
    @Column(nullable = false)
    private String status;

    @Builder.Default
    @Column(nullable = false)
    private boolean isKycSubmitted = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean isUrgent = false;

    // Optional fields for when the admin takes action
    private BigDecimal approvedAmount;
    private String adminNotes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
