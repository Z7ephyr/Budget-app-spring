package com.budgetapp.backend.dtos.expenses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseDTO {
    @NotBlank
    private String category;

    @Positive
    @NotNull
    private BigDecimal amount;

    private LocalDate date;
    private String notes;
    private boolean scanReceipt;
}