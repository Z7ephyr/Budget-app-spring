package com.budgetapp.backend.controllers;

import com.budgetapp.backend.dtos.expenses.CreateExpenseDTO;
import com.budgetapp.backend.dtos.expenses.ExpenseDTO;
import com.budgetapp.backend.config.UserDetailsImpl;
import com.budgetapp.backend.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; 

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Retrieves all expenses for the authenticated user.
     * The user ID is extracted securely from the authentication principal.
     * @param userDetails The authenticated user's details.
     * @return A ResponseEntity containing a list of ExpenseDTOs.
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpensesByUserId( // Changed return type to List<ExpenseDTO>
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // Get the ID from our custom UserDetails object
        List<ExpenseDTO> expenses = expenseService.getAllExpensesByUserId(userId); // Changed service method call
        return ResponseEntity.ok(expenses);
    }

    /**
     * Creates a new expense for the authenticated user.
     * @param userDetails The authenticated user's details.
     * @param createExpenseDTO The DTO containing the expense details to create.
     * @return A ResponseEntity containing the created ExpenseDTO and HttpStatus.CREATED.
     */
    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense( // Changed return type to ExpenseDTO
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @Valid @RequestBody CreateExpenseDTO createExpenseDTO) { // Changed request body to CreateExpenseDTO
        Long userId = userDetails.getId();
        ExpenseDTO addedExpense = expenseService.createExpense(createExpenseDTO, userId); // Changed service method call
        return new ResponseEntity<>(addedExpense, HttpStatus.CREATED);
    }

    /**
     * Updates an existing expense for the authenticated user.
     * @param userDetails The authenticated user's details.
     * @param expenseId The ID of the expense to update.
     * @param expenseDTO The DTO containing the updated expense details.
     * @return A ResponseEntity containing the updated ExpenseDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense( // Changed return type to ExpenseDTO
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable("id") Long expenseId,
                                                     @Valid @RequestBody ExpenseDTO expenseDTO) { // Changed request body to ExpenseDTO
        Long userId = userDetails.getId();
        ExpenseDTO updatedExpense = expenseService.updateExpense(expenseId, expenseDTO, userId); // Changed service method call
        return ResponseEntity.ok(updatedExpense);
    }

    /**
     * Deletes an expense for the authenticated user.
     * @param userDetails The authenticated user's details.
     * @param expenseId The ID of the expense to delete.
     * @return A ResponseEntity with HttpStatus.NO_CONTENT.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long expenseId) {
        Long userId = userDetails.getId();
        expenseService.deleteExpense(expenseId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a single expense by its ID for the authenticated user.
     * @param userDetails The authenticated user's details.
     * @param expenseId The ID of the expense to retrieve.
     * @return A ResponseEntity containing the ExpenseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById( // Changed return type to ExpenseDTO
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable("id") Long expenseId) {
        Long userId = userDetails.getId();
        Optional<ExpenseDTO> expense = expenseService.getExpenseById(expenseId, userId); // Changed service method call
        return expense.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
