package com.ezfinanz.los.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class LoanApplyRequest {

    @NotNull(message = "User ID is required")
    private Long userId; // Tying this manually for now until JWT is active

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.0", message = "Minimum loan amount is 1000")
    private BigDecimal requestedAmount;

    @NotNull(message = "Term months is required")
    private Integer termMonths;

    @NotBlank(message = "Purpose is required")
    private String purpose;
}
