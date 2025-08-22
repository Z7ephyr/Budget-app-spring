package com.budgetapp.backend.services;

import com.budgetapp.backend.dtos.expenses.CreateExpenseDTO;
import com.budgetapp.backend.dtos.expenses.ExpenseDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {

    /**
     * Creates a new expense for a user.
     * @param createExpenseDTO The DTO containing expense details from the 'Add Expense' screen.
     * @param userId The ID of the user creating the expense.
     * @return The created ExpenseDTO.
     */
    ExpenseDTO createExpense(CreateExpenseDTO createExpenseDTO, Long userId);

    /**
     * Retrieves a specific expense by its ID, ensuring it belongs to the specified user.
     * @param expenseId The ID of the expense.
     * @param userId The ID of the user requesting the expense.
     * @return An Optional containing the ExpenseDTO if found and owned by the user, empty otherwise.
     */
    Optional<ExpenseDTO> getExpenseById(Long expenseId, Long userId);

    /**
     * Retrieves a list of expenses for a user based on various search and filter criteria.
     * This is designed to power the 'Transactions' screen.
     * @param userId The ID of the user.
     * @param search A string to search for in expense descriptions (optional).
     * @param category The category name to filter by (optional).
     * @param date The date string to filter by (optional).
     * @param amountRange The amount range filter (e.g., "under50", "50to100", "over100") (optional).
     * @return A list of filtered ExpenseDTOs for the user.
     */
    List<ExpenseDTO> findExpensesByFilters(Long userId, String search, String category, String date, String amountRange);

    /**
     * Retrieves a list of all expenses for a user.
     * @param userId The ID of the user.
     * @return A list of ExpenseDTOs.
     */
    List<ExpenseDTO> getAllExpensesByUserId(Long userId);

    /**
     * Retrieves a list of transactions (simplified expense view) for a user within a given month.
     * This is typically used for the 'Transactions' screen.
     * @param userId The ID of the user.
     * @param month The month (YearMonth) for which to retrieve transactions.
     * @return A list of TransactionDTOs for the specified month.
     */
    List<TransactionDTO> getTransactionsByUserIdAndMonth(Long userId, YearMonth month);

    /**
     * Updates an existing expense.
     * @param expenseId The ID of the expense to update.
     * @param updatedExpenseDTO The updated expense data.
     * @param userId The ID of the user owning the expense (for security/validation).
     * @return The updated ExpenseDTO.
     */
    ExpenseDTO updateExpense(Long expenseId, ExpenseDTO updatedExpenseDTO, Long userId);

    /**
     * Deletes a specific expense by its ID, ensuring it belongs to the specified user.
     * @param expenseId The ID of the expense to delete.
     * @param userId The ID of the user owning the expense.
     */
    void deleteExpense(Long expenseId, Long userId);
}