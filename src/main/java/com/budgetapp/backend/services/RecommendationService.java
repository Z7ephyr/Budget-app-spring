package com.budgetapp.backend.services;

import java.math.BigDecimal;
import java.util.Map;

public interface RecommendationService {

    /**
     * Calculates AI budget recommendations and last month's spending for all categories for a user.
     * The recommendation is a simple average of the last 3 months of spending.
     * @param userId The ID of the user.
     * @return A map where the key is the categoryId and the value is a map of "aiRecommendation" and "lastMonthComparison" amounts.
     */
    Map<Long, Map<String, BigDecimal>> calculateBudgetRecommendations(Long userId);
}