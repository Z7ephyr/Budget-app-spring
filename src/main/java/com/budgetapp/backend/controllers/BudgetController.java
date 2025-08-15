package com.budgetapp.backend.controllers;

import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import com.budgetapp.backend.dtos.budgets.BudgetWithRecommendationDTO; // <-- NEW IMPORT
import com.budgetapp.backend.config.UserDetailsImpl; // Import your custom UserDetailsImpl
import com.budgetapp.backend.services.BudgetService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import for @AuthenticationPrincipal
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map; // Import Map for consistent error responses
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // --- Create Budget ---
    // User ID is now securely extracted from the JWT
    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget( // Changed return type to BudgetDTO
                                                   @Valid @RequestBody BudgetDTO budgetDTO,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId(); // Get user ID from authenticated principal
            BudgetDTO createdBudget = budgetService.createBudget(budgetDTO, userId);
            return new ResponseEntity<>(createdBudget, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            // Return a consistent JSON error response
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Or Map.of("general", e.getMessage()) if you want a body
        } catch (EntityNotFoundException e) {
            // Return a consistent JSON error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or Map.of("general", e.getMessage()) if you want a body
        } catch (Exception e) {
            // Return a consistent JSON error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or Map.of("general", "Error creating budget: " + e.getMessage())
        }
    }

    // --- Get Budget by ID ---
    // User ID is now securely extracted from the JWT
    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(
            @PathVariable("id") Long budgetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // Get user ID from authenticated principal
        Optional<BudgetDTO> budget = budgetService.getBudgetById(budgetId, userId);
        return budget.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    // --- Get Budget for a specific month ---
    // User ID is now securely extracted from the JWT
    @GetMapping("/month")
    public ResponseEntity<BudgetDTO> getBudgetByMonth(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("year") int year,
            @RequestParam("month") int month) { // Month as 1-12
        Long userId = userDetails.getId(); // Get user ID from authenticated principal
        YearMonth targetMonth = YearMonth.of(year, month);
        Optional<BudgetDTO> budget = budgetService.getBudgetByUserIdAndMonth(userId, targetMonth);
        return budget.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // --- Get All Budgets for a User (Updated to include recommendations) ---
    // User ID is now securely extracted from the JWT
    @GetMapping
    public ResponseEntity<List<BudgetWithRecommendationDTO>> getAllBudgets( // <-- UPDATED RETURN TYPE
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // Get user ID from authenticated principal
        // The service now returns the new DTO
        List<BudgetWithRecommendationDTO> budgets = budgetService.getAllBudgetsByUserId(userId);
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }

    // --- Update Budget ---
    // User ID is now securely extracted from the JWT
    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget( // Changed return type to BudgetDTO
                                                   @PathVariable("id") Long budgetId,
                                                   @Valid @RequestBody BudgetDTO budgetDTO, // Use BudgetDTO for update if it matches your request body
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId(); // Get user ID from authenticated principal
            BudgetDTO updatedBudget = budgetService.updateBudget(budgetId, budgetDTO, userId);
            return new ResponseEntity<>(updatedBudget, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            // Return a consistent JSON error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or Map.of("general", e.getMessage()) if you want a body
        } catch (SecurityException e) {
            // Return a consistent JSON error response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Or Map.of("general", e.getMessage()) if you want a body
        } catch (IllegalArgumentException e) {
            // Return a consistent JSON error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Or Map.of("general", e.getMessage()) if you want a body
        } catch (Exception e) {
            // Return a consistent JSON error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or Map.of("general", "Error updating budget: " + e.getMessage())
        }
    }

    // --- Delete Budget ---
    // User ID is now securely extracted from the JWT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable("id") Long budgetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId(); // Get user ID from authenticated principal
            budgetService.deleteBudget(budgetId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (EntityNotFoundException e) {
            // Return ResponseEntity<Void> with status and no body
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Fixed: Use .build() for Void
        } catch (SecurityException e) {
            // Return ResponseEntity<Void> with status and no body
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Fixed: Use .build() for Void
        } catch (Exception e) {
            // Return ResponseEntity<Void> with status and no body
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Fixed: Use .build() for Void
        }
    }
}
