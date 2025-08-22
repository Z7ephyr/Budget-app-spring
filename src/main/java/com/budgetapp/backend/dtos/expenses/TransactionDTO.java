package com.budgetapp.backend.dtos.expenses;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionDTO {
    private Long id;


    private String merchant;

    @Positive @NotNull
    private BigDecimal amount;

    @PastOrPresent
    private LocalDate date;


    private CategoryDTO category;
}