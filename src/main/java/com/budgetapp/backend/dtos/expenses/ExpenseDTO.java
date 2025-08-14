package com.budgetapp.backend.dtos.expenses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseDTO {
    private Long id;
    private Long userId;
    @NotBlank
    private String category;

    @Positive @NotNull
    private BigDecimal amount;

    private LocalDate date;
    private String description;
}



