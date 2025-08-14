package com.budgetapp.backend.controllers;

import com.budgetapp.backend.dtos.budgets.BudgetOverviewDTO;
import com.budgetapp.backend.services.DashboardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth; // For handling month parameter

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Retrieves the comprehensive dashboard overview for the authenticated user for a given month.
     *
     * @param userId Placeholder for the authenticated user's ID. In a real app with Spring Security,
     * this would be retrieved from the security context (e.g., @AuthenticationPrincipal).
     * @param year The year for the dashboard data (e.g., 2024). Defaults to current year if not provided.
     * @param month The month for the dashboard data (1-12). Defaults to current month if not provided.
     * @return ResponseEntity with the BudgetOverviewDTO.
     */
    @GetMapping
    public ResponseEntity<BudgetOverviewDTO> getDashboardData(
            // --- Placeholder for authenticated userId. Will be replaced with Spring Security later. ---
            // For now, assume userId is passed as a request param for testing, or hardcode for dev.
            // Example: /api/dashboard?userId=1&year=2024&month=7
            @RequestParam(name = "userId") Long userId, // IMPORTANT: REPLACE THIS WITH @AuthenticationPrincipal UserDetails later
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month) {

        YearMonth targetMonth;
        if (year != null && month != null) {
            targetMonth = YearMonth.of(year, month);
        } else {
            targetMonth = YearMonth.now(); // Default to current month
        }

        try {
            BudgetOverviewDTO overview = dashboardService.getDashboardOverview(userId, targetMonth);
            return new ResponseEntity<>(overview, HttpStatus.OK); // 200 OK
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found (e.g., user not found)
        } catch (Exception e) {
            // Log the exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // --- Important Note on userId ---
    // When we implement Spring Security, we will modify this method signature to
    // automatically inject the authenticated user's ID without needing it as a @RequestParam.
    // Example: public ResponseEntity<BudgetOverviewDTO> getDashboardData(@AuthenticationPrincipal CustomUserDetails userDetails) {
    //              Long userId = userDetails.getId();
    //              ...
    //          }
    // For now, you will need to manually pass a userId in your API calls (e.g., in Postman).
}