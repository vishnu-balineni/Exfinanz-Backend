package com.ezfinanz.los.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_repayment_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @Column(nullable = false)
    private Integer installmentNumber;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private BigDecimal emiAmount;

    @Column(nullable = false)
    private BigDecimal principalComponent;

    @Column(nullable = false)
    private BigDecimal interestComponent;

    // Status: PENDING, PAID, OVERDUE
    @Builder.Default
    @Column(nullable = false)
    private String status = "PENDING";
}
