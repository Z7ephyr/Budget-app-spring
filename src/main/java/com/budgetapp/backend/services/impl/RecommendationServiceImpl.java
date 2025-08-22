package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.model.Expense;
import com.budgetapp.backend.repositories.ExpenseRepository;
import com.budgetapp.backend.repositories.BudgetRepository;
import com.budgetapp.backend.services.RecommendationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating budget recommendations based on past spending patterns.
 * This implementation uses historical spending data to suggest budgets for the upcoming month.
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public RecommendationServiceImpl(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Calculates budget recommendations for a user based on their spending over the last three months.
     * The method returns a map containing a recommendation and last month's spending for each category.
     *
     * @param userId The ID of the user.
     * @return A map with category IDs as keys and a nested map of recommendation data as values.
     */
    @Override
    public Map<Long, Map<String, BigDecimal>> calculateBudgetRecommendations(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
        LocalDate threeMonthsAgoStart = now.minusMonths(3).withDayOfMonth(1);

        Map<Long, Map<String, BigDecimal>> recommendations = new HashMap<>();


        for (long categoryId = 1; categoryId <= 12; categoryId++) {


            BigDecimal totalHistoricalExpenses = expenseRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                    userId, categoryId, threeMonthsAgoStart, lastMonthEnd);

            BigDecimal aiRecommendation = BigDecimal.ZERO;
            if (totalHistoricalExpenses != null) {

                aiRecommendation = totalHistoricalExpenses.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
            }


            BigDecimal lastMonthComparison = expenseRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                    userId, categoryId, lastMonthStart, lastMonthEnd);

            if (lastMonthComparison == null) {
                lastMonthComparison = BigDecimal.ZERO;
            }


            Map<String, BigDecimal> categoryData = new HashMap<>();
            categoryData.put("aiRecommendation", aiRecommendation);
            categoryData.put("lastMonthComparison", lastMonthComparison);

            recommendations.put(categoryId, categoryData);
        }

        return recommendations;
    }
}
