package com.budgetapp.backend.services;
import com.budgetapp.backend.dtos.budgets.BudgetOverviewDTO;
import java.time.YearMonth; // For monthly context

public interface DashboardService {

    BudgetOverviewDTO getDashboardOverview(Long userId, YearMonth month);


}