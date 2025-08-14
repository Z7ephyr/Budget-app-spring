package com.budgetapp.backend.services;

import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    /**
     * Creates a new budget for a specific user and month.
     * Throws an exception if a budget for that user and month already exists.
     * @param budgetDTO The budget data to create.
     * @param userId The ID of the user creating the budget.
     * @return The created BudgetDTO.
     */
    BudgetDTO createBudget(BudgetDTO budgetDTO, Long userId);

    /**
     * Retrieves a specific budget by its ID, ensuring it belongs to the specified user.
     * @param budgetId The ID of the budget.
     * @param userId The ID of the user requesting the budget.
     * @return An Optional containing the BudgetDTO if found and owned by the user, empty otherwise.
     */
    Optional<BudgetDTO> getBudgetById(Long budgetId, Long userId);

    /**
     * Retrieves a budget for a specific user and month.
     * @param userId The ID of the user.
     * @param month The month (YearMonth) for which to retrieve the budget.
     * @return An Optional containing the BudgetDTO if found, empty otherwise.
     */
    Optional<BudgetDTO> getBudgetByUserIdAndMonth(Long userId, YearMonth month);

    /**
     * Retrieves all budgets for a specific user.
     * @param userId The ID of the user.
     * @return A list of BudgetDTOs for the user.
     */
    List<BudgetDTO> getAllBudgetsByUserId(Long userId);

    /**
     * Updates an existing budget.
     * @param budgetId The ID of the budget to update.
     * @param updatedBudgetDTO The updated budget data.
     * @param userId The ID of the user owning the budget (for security/validation).
     * @return The updated BudgetDTO.
     */
    BudgetDTO updateBudget(Long budgetId, BudgetDTO updatedBudgetDTO, Long userId);

    /**
     * Deletes a specific budget by its ID, ensuring it belongs to the specified user.
     * @param budgetId The ID of the budget to delete.
     * @param userId The ID of the user owning the budget.
     */
    void deleteBudget(Long budgetId, Long userId);
}