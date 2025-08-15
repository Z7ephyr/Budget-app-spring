package com.budgetapp.backend.repositories;

import com.budgetapp.backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph; // Import EntityGraph

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @EntityGraph(attributePaths = "user") // <-- This is the crucial fix for LazyInitializationException
    List<Expense> findByUserId(Long userId);

    /**
     * Finds expenses for a user within a specific date range.
     * @param userId The ID of the user.
     * @param start The start date of the range (inclusive).
     * @param end The end date of the range (inclusive).
     * @return A list of Expense entities within the specified date range.
     */
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    /**
     * Sums the amount of expenses for a specific user within a date range.
     * @param userId The ID of the user.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return An Optional containing the sum of amounts, or empty if no expenses found.
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> sumAmountByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Calculates the sum of amounts by category for a user within a date range.
     * @param userId The ID of the user.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return A list of Object arrays, where each array contains a category (String) and the sum of amounts (BigDecimal).
     */
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate GROUP BY e.category")
    List<Object[]> sumAmountByCategoryByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Finds recent transactions for a user within a date range, ordered by date descending.
     * @param userId The ID of the user.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return A list of Expense entities, ordered by date in descending order.
     */
    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Finds an expense by its ID and ensures it belongs to a specific user.
     * This is crucial for security to prevent users from accessing/modifying others' expenses.
     * @param id The ID of the expense.
     * @param userId The ID of the user who owns the expense.
     * @return An Optional containing the Expense entity if found and owned by the user.
     */
    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    /**
     * Checks if an expense with the given ID exists and is owned by the specified user.
     * This method is used for security checks, particularly before deletion.
     * @param id The ID of the expense.
     * @param userId The ID of the user who is expected to own the expense.
     * @return True if the expense exists and is owned by the user, false otherwise.
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Finds all expenses for a given user, category, and within a date range.
     * This is the crucial new method required for AI budget recommendations.
     * @param userId The ID of the user.
     * @param category The name of the expense category.
     * @param startDate The start date of the historical period.
     * @param endDate The end date of the historical period.
     * @return A list of Expenses matching the criteria.
     */
    List<Expense> findByUserIdAndCategoryAndDateBetween(Long userId, String category, LocalDate startDate, LocalDate endDate);
}
