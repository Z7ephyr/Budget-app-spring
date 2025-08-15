package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.budgets.BudgetOverviewDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import com.budgetapp.backend.model.Budget;
import com.budgetapp.backend.model.User;
import com.budgetapp.backend.repositories.BudgetRepository;
import com.budgetapp.backend.repositories.ExpenseRepository;
import com.budgetapp.backend.repositories.UserRepository;
import com.budgetapp.backend.mappers.ExpenseMapper;
import com.budgetapp.backend.services.DashboardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    public DashboardServiceImpl(BudgetRepository budgetRepository, ExpenseRepository expenseRepository, UserRepository userRepository, ExpenseMapper expenseMapper) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetOverviewDTO getDashboardOverview(Long userId, YearMonth month) {
        // Ensure the user exists (good practice for security/data integrity)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Define start and end dates for the month
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        // 1. Fetch Monthly Budgets
        // The repository method now returns a List.
        List<Budget> monthlyBudgets = budgetRepository.findByUserIdAndMonthStart(userId, startDate);

        // Sum the amounts of all budgets for the month. If the list is empty, the sum is zero.
        BigDecimal monthlyBudgetAmount = monthlyBudgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Calculate Total Spending for the Month
        BigDecimal totalSpending = expenseRepository.sumAmountByUserIdAndDateBetween(userId, startDate, endDate)
                .orElse(BigDecimal.ZERO);

        // 3. Calculate Spending by Category for the Month
        Map<String, BigDecimal> spendingByCategory = expenseRepository.sumAmountByCategoryByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0], // Category name
                        obj -> (BigDecimal) obj[1] // Summed amount
                ));

        // 4. Fetch Recent Transactions (e.g., top 5 or 10)
        List<TransactionDTO> recentTransactions = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate)
                .stream()
                .limit(5) // Get top 5 recent transactions
                .map(expenseMapper::toTransactionDto)
                .collect(Collectors.toList());

        // 5. Calculate Total Balance (Simplified: Remaining budget)
        BigDecimal totalBalance = monthlyBudgetAmount.subtract(totalSpending);

        // 6. Calculate Spending Percentage
        BigDecimal spendingPercentage = BigDecimal.ZERO;
        if (monthlyBudgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            spendingPercentage = totalSpending.divide(monthlyBudgetAmount, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return BudgetOverviewDTO.builder()
                .totalBalance(totalBalance)
                .monthlyBudget(monthlyBudgetAmount)
                .spendingByCategory(spendingByCategory)
                .recentTransactions(recentTransactions)
                .spendingPercentage(spendingPercentage)
                .build();
    }
}
