package com.budgetapp.backend.services;

import com.budgetapp.backend.dtos.expenses.CreateExpenseDTO;
import com.budgetapp.backend.dtos.expenses.ExpenseDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;

import java.time.LocalDate;
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
     * Retrieves all expenses for a specific user.
     * @param userId The ID of the user.
     * @return A list of ExpenseDTOs for the user.
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
