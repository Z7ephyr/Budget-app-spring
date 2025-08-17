package com.budgetapp.backend.dtos.budgets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO to transfer budget data along with AI recommendation and last month's spending.
 * Every budget category will have an AI recommendation and last month's comparison.
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

    // AI Recommendation for the category
    private BigDecimal aiRecommendation;

    // Comparison with last month's spending
    private BigDecimal lastMonthComparison;

    // Optional recommendation text for frontend display
    private String recommendationNote;
}
