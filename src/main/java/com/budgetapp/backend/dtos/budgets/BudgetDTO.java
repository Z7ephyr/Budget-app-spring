package com.budgetapp.backend.dtos.budgets;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BudgetDTO {
    private Long id;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Month start date is required")
    private LocalDate monthStart;

    private BigDecimal aiRecommendation;
    private BigDecimal lastMonthComparison;
    private String recommendationNote;
}