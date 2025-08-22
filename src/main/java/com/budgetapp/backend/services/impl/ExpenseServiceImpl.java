package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.alerts.AlertDTO;
import com.budgetapp.backend.dtos.expenses.CreateExpenseDTO;
import com.budgetapp.backend.dtos.expenses.ExpenseDTO;
import com.budgetapp.backend.dtos.expenses.TransactionDTO;
import com.budgetapp.backend.mappers.ExpenseMapper;
import com.budgetapp.backend.model.Budget;
import com.budgetapp.backend.model.Category;
import com.budgetapp.backend.model.Expense;
import com.budgetapp.backend.model.User;
import com.budgetapp.backend.repositories.BudgetRepository;
import com.budgetapp.backend.repositories.CategoryRepository;
import com.budgetapp.backend.repositories.ExpenseRepository;
import com.budgetapp.backend.repositories.UserRepository;
import com.budgetapp.backend.services.AlertService;
import com.budgetapp.backend.services.ExpenseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;
    private final AlertService alertService;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            ExpenseMapper expenseMapper,
            AlertService alertService,
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.expenseMapper = expenseMapper;
        this.alertService = alertService;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ExpenseDTO createExpense(CreateExpenseDTO createExpenseDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Category category = categoryRepository.findById(createExpenseDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + createExpenseDTO.getCategoryId()));

        Expense expense = expenseMapper.toEntity(createExpenseDTO);

        expense.setUser(user);
        expense.setCategory(category);

        Expense savedExpense = expenseRepository.save(expense);

        checkBudgetAndCreateAlert(savedExpense);

        return expenseMapper.toDto(savedExpense);
    }

    private void checkBudgetAndCreateAlert(Expense newExpense) {
        YearMonth currentMonth = YearMonth.from(newExpense.getDate());
        Long categoryId = newExpense.getCategory().getId();
        Optional<Budget> budgetOptional = budgetRepository.findByUserIdAndMonthStartAndCategoryId(
                newExpense.getUser().getId(),
                currentMonth.atDay(1),
                categoryId
        );

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();
            BigDecimal totalSpending = expenseRepository.sumAmountByUserIdAndCategoryIdAndDateBetween(
                    newExpense.getUser().getId(),
                    categoryId,
                    currentMonth.atDay(1),
                    currentMonth.atEndOfMonth()
            );

            if (totalSpending != null && totalSpending.compareTo(budget.getAmount()) > 0) {
                AlertDTO alertDTO = AlertDTO.builder()
                        .type("budgetoverrun")
                        .message("You have exceeded your '" + budget.getCategory().getName() + "' budget of $" + budget.getAmount() + ". Your total spending is now $" + totalSpending + ".")
                        .build();
                alertService.createAlert(alertDTO, newExpense.getUser().getId());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> findExpensesByFilters(Long userId, String search, String category, String date, String amountRange) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        BigDecimal minAmount = null;
        BigDecimal maxAmount = null;

        if (date != null && !date.isEmpty()) {
            LocalDate filterDate = LocalDate.parse(date);
            startDate = filterDate.withDayOfMonth(1);
            endDate = filterDate.withDayOfMonth(filterDate.lengthOfMonth());
        }

        if (amountRange != null && !amountRange.isEmpty()) {
            switch (amountRange) {
                case "under50":
                    maxAmount = new BigDecimal(49.99);
                    break;
                case "50to100":
                    minAmount = new BigDecimal(50.00);
                    maxAmount = new BigDecimal(100.00);
                    break;
                case "over100":
                    minAmount = new BigDecimal(100.01);
                    break;
            }
        }

        List<Object[]> results = expenseRepository.findExpensesByFilters(userId, search, category, startDate, endDate, minAmount, maxAmount);

        List<ExpenseDTO> dtoList = new ArrayList<>();
        for (Object[] row : results) {
            ExpenseDTO dto =  new ExpenseDTO();


            dto.setId(((Number) row[0]).longValue());
            dto.setAmount((BigDecimal) row[1]);


            java.sql.Date sqlDate = (java.sql.Date) row[2];
            if (sqlDate != null) {
                dto.setDate(sqlDate.toLocalDate());
            } else {
                dto.setDate(null);
            }

            dto.setDescription((String) row[3]);
            dto.setUserId(((Number) row[4]).longValue());
            dto.setCategoryId(((Number) row[6]).longValue());
            dto.setCategoryName((String) row[7]);
            dto.setCategoryDescription((String) row[8]);

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseDTO> getExpenseById(Long expenseId, Long userId) {
        return expenseRepository.findByIdAndUserId(expenseId, userId)
                .map(expenseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpensesByUserId(Long userId) {
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
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        return expenses.stream()
                .map(expenseMapper::toTransactionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExpenseDTO updateExpense(Long expenseId, ExpenseDTO updatedExpenseDTO, Long userId) {
        Expense existingExpense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found or not owned by user with ID: " + expenseId));
        expenseMapper.updateEntityFromDto(updatedExpenseDTO, existingExpense);
        Expense savedExpense = expenseRepository.save(existingExpense);
        return expenseMapper.toDto(savedExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        if (!expenseRepository.existsByIdAndUserId(expenseId, userId)) {
            throw new EntityNotFoundException("Expense not found or not owned by user with ID: " + expenseId);
        }
        expenseRepository.deleteById(expenseId);
    }
}