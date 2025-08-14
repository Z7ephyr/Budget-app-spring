package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import com.budgetapp.backend.mappers.BudgetMapper;
import com.budgetapp.backend.model.Budget;
import com.budgetapp.backend.model.User;
import com.budgetapp.backend.repositories.BudgetRepository;
import com.budgetapp.backend.repositories.UserRepository;
import com.budgetapp.backend.services.BudgetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;

    public BudgetServiceImpl(BudgetRepository budgetRepository, UserRepository userRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.budgetMapper = budgetMapper;
    }

    @Override
    @Transactional
    public BudgetDTO createBudget(BudgetDTO budgetDTO, Long userId) {
        // 1. Fetch the User entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Define the month start date for the budget
        LocalDate monthStart = budgetDTO.getMonthStart();
        if (monthStart == null) {
            // Default to current month's start if not provided in DTO for creation
            monthStart = YearMonth.now().atDay(1);
            budgetDTO.setMonthStart(monthStart); // Ensure DTO also reflects this
        } else {
            // Ensure the provided monthStart is actually the first day of the month
            monthStart = YearMonth.from(monthStart).atDay(1);
            budgetDTO.setMonthStart(monthStart);
        }

        // 2. Check if a budget for this user and month already exists
        if (budgetRepository.findByUserIdAndMonthStart(userId, monthStart).isPresent()) {
            throw new IllegalArgumentException("A budget already exists for this user and month: " + monthStart);
        }

        // 3. Map DTO to entity and set user
        Budget budget = budgetMapper.toEntity(budgetDTO);
        budget.setUser(user);

        // 4. Save and map back to DTO
        Budget savedBudget = budgetRepository.save(budget);
        return budgetMapper.toDto(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetDTO> getBudgetById(Long budgetId, Long userId) {
        return budgetRepository.findById(budgetId)
                .filter(budget -> budget.getUser().getId().equals(userId)) // Security check: ensure user owns the budget
                .map(budgetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetDTO> getBudgetByUserIdAndMonth(Long userId, YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        return budgetRepository.findByUserIdAndMonthStart(userId, monthStart)
                .map(budgetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDTO> getAllBudgetsByUserId(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
                .map(budgetMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BudgetDTO updateBudget(Long budgetId, BudgetDTO updatedBudgetDTO, Long userId) {
        Budget existingBudget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with ID: " + budgetId));

        // Security check: ensure the budget belongs to the requesting user
        if (!existingBudget.getUser().getId().equals(userId)) {
            throw new SecurityException("Access Denied: Budget does not belong to user ID: " + userId);
        }

        // Ensure monthStart cannot be changed on update (or handle carefully if it can)
        // If monthStart in DTO is different from existing, it means user is trying to change month,
        // which typically implies creating a *new* budget, not updating an old one.
        if (updatedBudgetDTO.getMonthStart() != null &&
                !existingBudget.getMonthStart().equals(YearMonth.from(updatedBudgetDTO.getMonthStart()).atDay(1))) {
            throw new IllegalArgumentException("Cannot change budget month during update. Create a new budget for a different month.");
        }

        // Map updated fields from DTO to existing entity
        budgetMapper.updateEntityFromDto(updatedBudgetDTO, existingBudget);

        // Save the updated entity
        Budget savedBudget = budgetRepository.save(existingBudget);
        return budgetMapper.toDto(savedBudget);
    }

    @Override
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budgetToDelete = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with ID: " + budgetId));

        // Security check: ensure the budget belongs to the requesting user
        if (!budgetToDelete.getUser().getId().equals(userId)) {
            throw new SecurityException("Access Denied: Budget does not belong to user ID: " + userId);
        }

        budgetRepository.delete(budgetToDelete);
    }
}