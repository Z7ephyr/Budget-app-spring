package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.model.Budget;
import com.budgetapp.backend.model.Expense;
import com.budgetapp.backend.repositories.BudgetRepository;
import com.budgetapp.backend.repositories.ExpenseRepository;
import com.budgetapp.backend.services.RecommendationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public RecommendationServiceImpl(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Calculates AI budget recommendations and last month's spending for all categories for a user.
     * The recommendation is a simple average of the last 3 months of spending.
     * @param userId The ID of the user.
     * @return A map where the key is the categoryId and the value is a map of "aiRecommendation" and "lastMonthComparison" amounts.
     */
    @Override
    public Map<Long, Map<String, BigDecimal>> calculateBudgetRecommendations(Long userId) {
        // Define the time periods for analysis.
        LocalDate now = LocalDate.now();
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
        LocalDate threeMonthsAgoStart = now.minusMonths(3).withDayOfMonth(1);

        // Fetch all budgets for the user to get a list of all categories they manage.
        List<Budget> userBudgets = budgetRepository.findByUserId(userId);

        Map<Long, Map<String, BigDecimal>> recommendations = new HashMap<>();

        for (Budget budget : userBudgets) {
            // Get the category name using the mapping from the category ID.
            String categoryName = getCategoryNameById(budget.getCategoryId());

            // 1. Calculate the AI Recommendation (Average of last 3 months)
            List<Expense> historicalExpenses = expenseRepository.findByUserIdAndCategoryAndDateBetween(
                    userId, categoryName, threeMonthsAgoStart, lastMonthEnd);

            BigDecimal totalSpentThreeMonths = historicalExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal aiRecommendation = BigDecimal.ZERO;
            if (historicalExpenses.size() > 0) {
                // Divide the total by the number of months to get a monthly average.
                // We use a scale of 2 for currency and RoundingMode.HALF_UP.
                aiRecommendation = totalSpentThreeMonths.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
            }

            // 2. Calculate the Last Month's Spending
            List<Expense> lastMonthExpenses = expenseRepository.findByUserIdAndCategoryAndDateBetween(
                    userId, categoryName, lastMonthStart, lastMonthEnd);

            BigDecimal lastMonthComparison = lastMonthExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3. Store the results in the final map
            Map<String, BigDecimal> categoryData = new HashMap<>();
            categoryData.put("aiRecommendation", aiRecommendation);
            categoryData.put("lastMonthComparison", lastMonthComparison);
            recommendations.put(budget.getCategoryId(), categoryData);
        }

        return recommendations;
    }

    /**
     * A helper method to map a category ID to its name.
     * This is used since there is no dedicated Category entity.
     * @param categoryId The ID of the category.
     * @return The corresponding category name.
     */
    private String getCategoryNameById(Long categoryId) {
        return switch (categoryId.intValue()) {
            case 1 -> "Groceries";
            case 2 -> "Dining Out";
            case 3 -> "Transportation";
            case 4 -> "Housing";
            case 5 -> "Shopping";
            case 6 -> "Entertainment";
            case 7 -> "Health";
            case 8 -> "Pets";
            case 9 -> "Education";
            case 10 -> "Work";
            case 11 -> "Gifts";
            case 12 -> "Other";
            default -> "Unknown";
        };
    }
}