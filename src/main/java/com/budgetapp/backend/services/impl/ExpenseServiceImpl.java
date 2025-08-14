package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.expenses.CreateExpenseDTO;
import com.budgetapp.backend.dtos.expenses.ExpenseDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import com.budgetapp.backend.mappers.ExpenseMapper;
import com.budgetapp.backend.model.Expense;
import com.budgetapp.backend.model.User;
import com.budgetapp.backend.repositories.ExpenseRepository;
import com.budgetapp.backend.repositories.UserRepository;
import com.budgetapp.backend.services.ExpenseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Transactional
    public ExpenseDTO createExpense(CreateExpenseDTO createExpenseDTO, Long userId) {
        // 1. Fetch the User entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // 2. Map DTO to entity
        Expense expense = expenseMapper.toEntity(createExpenseDTO);

        // 3. Set the User entity on the Expense
        expense.setUser(user);

        // Handle scanReceipt field from CreateExpenseDTO if applicable (e.g., integrate with a file storage service)
        // For now, this logic is omitted as it depends on your file handling strategy.
        // If 'scanReceipt' is just a boolean, it's mapped. If it's a file, it's a separate process.

        // 4. Save and map back to ExpenseDTO for response
        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toDto(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseDTO> getExpenseById(Long expenseId, Long userId) {
        // Ensure the findByIdAndUserId method exists in your ExpenseRepository
        // If not, you might need to add: Optional<Expense> findByIdAndUserId(Long id, Long userId);
        return expenseRepository.findByIdAndUserId(expenseId, userId) // Changed to findByIdAndUserId for security
                .map(expenseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpensesByUserId(Long userId) {
        // Ensure @EntityGraph(attributePaths = "user") is on findByUserId in ExpenseRepository
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return expenses.stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByUserIdAndMonth(Long userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();
        // Use the repository method we refined earlier
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        return expenses.stream()
                .map(expenseMapper::toTransactionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExpenseDTO updateExpense(Long expenseId, ExpenseDTO updatedExpenseDTO, Long userId) {
        // Ensure the findByIdAndUserId method exists in your ExpenseRepository
        Expense existingExpense = expenseRepository.findByIdAndUserId(expenseId, userId) // Changed to findByIdAndUserId for security
                .orElseThrow(() -> new EntityNotFoundException("Expense not found or not owned by user with ID: " + expenseId));

        // No explicit security check needed here as findByIdAndUserId already filters by user.
        // If you want to throw a more specific SecurityException, you could keep the filter.
        // if (!existingExpense.getUser().getId().equals(userId)) {
        //     throw new SecurityException("Access Denied: Expense does not belong to user ID: " + userId);
        // }

        // Map updated fields from DTO to existing entity
        expenseMapper.updateEntityFromDto(updatedExpenseDTO, existingExpense);

        // Save and map back to DTO
        Expense savedExpense = expenseRepository.save(existingExpense);
        return expenseMapper.toDto(savedExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        // Ensure the existsByIdAndUserId method exists in your ExpenseRepository
        // If not, you might need to add: boolean existsByIdAndUserId(Long id, Long userId);
        if (!expenseRepository.existsByIdAndUserId(expenseId, userId)) { // Changed to existsByIdAndUserId for security
            throw new EntityNotFoundException("Expense not found or not owned by user with ID: " + expenseId);
        }

        expenseRepository.deleteById(expenseId);
    }
}
