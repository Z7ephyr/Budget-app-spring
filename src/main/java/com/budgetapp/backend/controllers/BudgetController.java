package com.budgetapp.backend.controllers;

import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import com.budgetapp.backend.dtos.budgets.CreateBudgetDTO;
import com.budgetapp.backend.config.UserDetailsImpl;
import com.budgetapp.backend.services.BudgetService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }


    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(
            @Valid @RequestBody CreateBudgetDTO createBudgetDTO, // Use CreateBudgetDTO for input
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId();
            BudgetDTO createdBudget = budgetService.createBudget(createBudgetDTO, userId);
            return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<BudgetDTO> getBudgetById(
            @PathVariable("id") Long budgetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        Optional<BudgetDTO> budget = budgetService.getBudgetById(budgetId, userId);
        return budget.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping("/month")
    public ResponseEntity<BudgetDTO> getBudgetByMonth(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        Long userId = userDetails.getId();
        YearMonth targetMonth = YearMonth.of(year, month);
        Optional<BudgetDTO> budget = budgetService.getBudgetByUserIdAndMonth(userId, targetMonth);
        return budget.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping
    public ResponseEntity<List<BudgetDTO>> getAllBudgets(
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        List<BudgetDTO> budgets = budgetService.getAllBudgetsByUserId(userId);
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(
            @PathVariable("id") Long budgetId,
            @Valid @RequestBody CreateBudgetDTO updatedBudgetDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId();
            BudgetDTO updatedBudget = budgetService.updateBudget(budgetId, updatedBudgetDTO, userId);
            return new ResponseEntity<>(updatedBudget, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable("id") Long budgetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId();
            budgetService.deleteBudget(budgetId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}