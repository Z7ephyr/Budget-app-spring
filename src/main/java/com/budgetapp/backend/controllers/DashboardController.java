package com.budgetapp.backend.controllers;
import com.budgetapp.backend.dtos.budgets.BudgetOverviewDTO; // Add this import
import com.budgetapp.backend.services.DashboardService;
import com.budgetapp.backend.config.UserDetailsImpl; // Import your UserDetails class
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import the annotation
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Retrieves the comprehensive dashboard overview for the authenticated user for a given month.
     * The user's ID is automatically injected by Spring Security.
     *
     * @param userDetails The authenticated user's details, provided by Spring Security.
     * @param year        The year for the dashboard data (e.g., 2024). Defaults to current year if not provided.
     * @param month       The month for the dashboard data (1-12). Defaults to current month if not provided.
     * @return ResponseEntity with the BudgetOverviewDTO.
     */
    @GetMapping
    public ResponseEntity<BudgetOverviewDTO> getDashboardData(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month) {

        Long userId = userDetails.getId(); // Get user ID securely from the token

        YearMonth targetMonth;
        if (year != null && month != null) {
            targetMonth = YearMonth.of(year, month);
        } else {
            targetMonth = YearMonth.now(); // Default to current month
        }

        try {
            BudgetOverviewDTO overview = dashboardService.getDashboardOverview(userId, targetMonth);
            return new ResponseEntity<>(overview, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Log the exception for debugging
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}