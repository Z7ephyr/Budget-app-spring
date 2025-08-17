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

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public RecommendationServiceImpl(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    public Map<Long, Map<String, BigDecimal>> calculateBudgetRecommendations(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
        LocalDate threeMonthsAgoStart = now.minusMonths(3).withDayOfMonth(1);

        Map<Long, Map<String, BigDecimal>> recommendations = new HashMap<>();

        // Loop through all category IDs (1–12)
        for (long categoryId = 1; categoryId <= 12; categoryId++) {
            String categoryName = getCategoryNameById(categoryId);

            // 1. Historical expenses for last 3 months
            List<Expense> historicalExpenses = expenseRepository.findByUserIdAndCategoryAndDateBetween(
                    userId, categoryName, threeMonthsAgoStart, lastMonthEnd);

            BigDecimal aiRecommendation = historicalExpenses.isEmpty() ? BigDecimal.ZERO :
                    historicalExpenses.stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);

            // 2. Last month's expenses
            List<Expense> lastMonthExpenses = expenseRepository.findByUserIdAndCategoryAndDateBetween(
                    userId, categoryName, lastMonthStart, lastMonthEnd);

            BigDecimal lastMonthComparison = lastMonthExpenses.isEmpty() ? BigDecimal.ZERO :
                    lastMonthExpenses.stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3. Store in map
            Map<String, BigDecimal> categoryData = new HashMap<>();
            categoryData.put("aiRecommendation", aiRecommendation);
            categoryData.put("lastMonthComparison", lastMonthComparison);

            recommendations.put(categoryId, categoryData);
        }

        return recommendations;
    }

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
