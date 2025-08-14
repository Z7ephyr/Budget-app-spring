package com.budgetapp.backend.dtos.expenses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionDTO {
    private Long id;

    @NotBlank
    private String merchant;  // e.g. "Grocery Store"

    @NotBlank
    private String category;  // e.g. "Groceries"

    @Positive @NotNull
    private BigDecimal amount;

    @PastOrPresent
    private LocalDate date;
}