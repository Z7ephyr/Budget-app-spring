package com.budgetapp.backend.repositories;

import com.budgetapp.backend.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Finds all budgets belonging to a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Budget entities.
     */
    List<Budget> findByUserId(Long userId);

    /**
     * Finds a budget for a specific user and month start date.
     * This method is updated to return a list, as a user can have multiple budgets per month.
     *
     * @param userId The ID of the user.
     * @param monthStart The start date of the month (e.g., '2025-07-01').
     * @return A list of Budget entities for the specified month.
     */
    List<Budget> findByUserIdAndMonthStart(Long userId, LocalDate monthStart);

    /**
     * Finds a budget for a specific user, month start date, and category ID.
     *
     * @param userId The ID of the user.
     * @param monthStart The start date of the month.
     * @param categoryId The ID of the category.
     * @return An Optional containing the Budget entity if found, otherwise empty.
     */
    Optional<Budget> findByUserIdAndMonthStartAndCategoryId(Long userId, LocalDate monthStart, Long categoryId);

    /**
     * Finds all budgets for a user within a specified date range.
     *
     * @param userId The ID of the user.
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A list of Budget entities.
     */
    List<Budget> findByUserIdAndMonthStartBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Finds a budget by its ID and ensures it belongs to a specific user.
     * This is a crucial security check.
     * @param id The ID of the budget.
     * @param userId The ID of the user.
     * @return An Optional containing the Budget entity if found and owned by the user.
     */
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    /**
     * Checks if an expense with the given ID exists and is owned by the specified user.
     * @param id The ID of the budget.
     * @param userId The ID of the user.
     * @return True if the budget exists and is owned by the user, false otherwise.
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}
