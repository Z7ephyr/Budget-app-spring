package com.budgetapp.backend.dtos.budgets;

import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class BudgetOverviewDTO {
    private BigDecimal totalBalance;
    private BigDecimal monthlyBudget;
    private Map<String, BigDecimal> spendingByCategory;
    private List<TransactionDTO> recentTransactions;
    private BigDecimal spendingPercentage;
}