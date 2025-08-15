package com.budgetapp.backend.dtos.budgets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDate;

/**
 * DTO to transfer budget data along with AI recommendation and last month's spending.
 * This is the object that will be sent to the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetWithRecommendationDTO {
    private Long id;
    private Long categoryId;
    private LocalDate monthStart;
    private BigDecimal amount;

    // AI Recommendation fields
    private BigDecimal aiRecommendation;
    private BigDecimal lastMonthComparison;
}