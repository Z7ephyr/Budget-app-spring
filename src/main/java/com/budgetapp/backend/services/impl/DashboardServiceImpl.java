package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.budgets.BudgetOverviewDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import com.budgetapp.backend.model.Budget;
import com.budgetapp.backend.model.User; // If needed for getting User reference
import com.budgetapp.backend.repositories.BudgetRepository;
import com.budgetapp.backend.repositories.ExpenseRepository;
import com.budgetapp.backend.repositories.UserRepository; // Might need to fetch User entity for some queries
import com.budgetapp.backend.mappers.ExpenseMapper; // To map Expenses to TransactionDTOs
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
    private final UserRepository userRepository; // To fetch User if needed for repository queries
    private final ExpenseMapper expenseMapper; // To map recent transactions to DTO

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

        // 1. Fetch Monthly Budget
        // Assuming a user has one budget per month/YearMonth
        Optional<Budget> monthlyBudgetOptional = budgetRepository.findByUserIdAndMonthStart(userId, startDate);
        BigDecimal monthlyBudgetAmount = monthlyBudgetOptional.map(Budget::getAmount).orElse(BigDecimal.ZERO);

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
        // You'll need a method like findTopNByUserIdOrderByDateDesc in ExpenseRepository for this.
        // For simplicity, let's fetch a few most recent expenses from the current month
        List<TransactionDTO> recentTransactions = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate)
                .stream()
                .limit(5) // Get top 5 recent transactions
                .map(expenseMapper::toTransactionDto)
                .collect(Collectors.toList());

        // 5. Calculate Total Balance (This is more complex, usually involves incomes - expenses)
        // For now, let's assume total balance is not directly managed as a single field
        // and might be derived from a running sum of all transactions or an actual 'Account' entity.
        // If you don't have an 'Income' entity yet, totalBalance calculation might be limited.
        // Let's approximate it or leave it as a placeholder. For now, we'll make it 0 or derive from budget vs spending.
        // If "total balance" implies current account balance, you'd need an 'Account' entity or a sum of all transactions.
        // For this DTO, let's interpret 'totalBalance' as remaining budget or a conceptual balance relative to the budget.
        BigDecimal totalBalance = monthlyBudgetAmount.subtract(totalSpending); // Simplified: Remaining budget

        // 6. Calculate Spending Percentage
        BigDecimal spendingPercentage = BigDecimal.ZERO;
        if (monthlyBudgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            spendingPercentage = totalSpending.divide(monthlyBudgetAmount, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }


        return BudgetOverviewDTO.builder()
                .totalBalance(totalBalance) // This interpretation needs clarification from you
                .monthlyBudget(monthlyBudgetAmount)
                .spendingByCategory(spendingByCategory)
                .recentTransactions(recentTransactions)
                .spendingPercentage(spendingPercentage)
                .build();
    }
}