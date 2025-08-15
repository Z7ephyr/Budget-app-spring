package com.budgetapp.backend.dtos.budgets;

import com.budgetapp.backend.model.Budget;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Data
@Builder
public class BudgetDTO {
    private Long id;

    // Add this new field
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // userId is not needed in the DTO as it's handled by the authenticated user
    // private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Month start date is required")
    private LocalDate monthStart;


    private Optional<BigDecimal> aiRecommendation;
    private Optional<BigDecimal> lastMonthComparison;
}
