package com.budgetapp.backend.dtos.budgets;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BudgetDTO {
    private Long id;
    private Long userId;

    @Positive @NotNull
    private BigDecimal amount;

    private LocalDate monthStart;


    private BigDecimal aiRecommendation;
    private BigDecimal lastMonthComparison;
}

