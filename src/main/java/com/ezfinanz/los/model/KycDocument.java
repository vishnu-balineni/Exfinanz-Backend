package com.ezfinanz.los.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link document to the User globally based on the new architecture
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // e.g., AADHAR, PAN_CARD, PAYSLIP, BANK_STATEMENT
    @Column(nullable = false)
    private String documentType;

    // File path or S3 URL
    @Column(nullable = false)
    private String documentUrl;

    // Status can be: PENDING_VERIFICATION, VERIFIED, REJECTED
    @Builder.Default
    @Column(nullable = false)
    private String verificationStatus = "PENDING_VERIFICATION";

    private String adminRemarks;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
