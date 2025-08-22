package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import com.budgetapp.backend.dtos.budgets.CreateBudgetDTO; // Use the new DTO for input
import com.budgetapp.backend.mappers.BudgetMapper;
import com.budgetapp.backend.model.Budget;
import com.budgetapp.backend.model.User;
import com.budgetapp.backend.repositories.BudgetRepository;
import com.budgetapp.backend.repositories.UserRepository;
import com.budgetapp.backend.services.BudgetService;
import com.budgetapp.backend.services.RecommendationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final UserRepository userRepository;
    private final RecommendationService recommendationService;

    public BudgetServiceImpl(BudgetRepository budgetRepository, BudgetMapper budgetMapper,
                             UserRepository userRepository, RecommendationService recommendationService) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
        this.userRepository = userRepository;
        this.recommendationService = recommendationService;
    }

    @Override
    @Transactional
    public BudgetDTO createBudget(CreateBudgetDTO createBudgetDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        LocalDate monthStart = createBudgetDTO.getMonthStart() != null ? YearMonth.from(createBudgetDTO.getMonthStart()).atDay(1)
                : YearMonth.now().atDay(1);
        createBudgetDTO.setMonthStart(monthStart);

        if (budgetRepository.findByUserIdAndMonthStartAndCategoryId(userId, monthStart, createBudgetDTO.getCategoryId()).isPresent()) {
            throw new IllegalArgumentException("A budget already exists for this user, month, and category: " + createBudgetDTO.getCategoryId());
        }

        Budget budget = budgetMapper.toEntity(createBudgetDTO);
        budget.setUser(user);
        return budgetMapper.toDto(budgetRepository.save(budget));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetDTO> getBudgetById(Long budgetId, Long userId) {
        return budgetRepository.findByIdAndUserId(budgetId, userId).map(budgetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetDTO> getBudgetByUserIdAndMonth(Long userId, YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        return budgetRepository.findByUserIdAndMonthStart(userId, monthStart).stream()
                .map(budgetMapper::toDto).findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetDTO> getAllBudgetsByUserId(Long userId) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);


        List<Budget> userBudgets = budgetRepository.findByUserIdAndMonthStart(userId, monthStart);
        Map<Long, Budget> budgetMap = userBudgets.stream()
                .collect(Collectors.toMap(budget -> budget.getCategory().getId(), budget -> budget));


        Map<Long, Map<String, BigDecimal>> recommendations = recommendationService.calculateBudgetRecommendations(userId);

        List<BudgetDTO> combinedBudgets = new ArrayList<>();
        for (long categoryId = 1; categoryId <= 12; categoryId++) {
            Budget existingBudget = budgetMap.get(categoryId);
            Map<String, BigDecimal> recData = recommendations.getOrDefault(categoryId, Map.of(
                    "aiRecommendation", BigDecimal.ZERO,
                    "lastMonthComparison", BigDecimal.ZERO
            ));

            BigDecimal aiRecommendation = recData.get("aiRecommendation");
            BigDecimal lastMonthComparison = recData.get("lastMonthComparison");


            BudgetDTO dto = BudgetDTO.builder()
                    .id(existingBudget != null ? existingBudget.getId() : null)
                    .categoryId(categoryId)
                    .monthStart(existingBudget != null ? existingBudget.getMonthStart() : monthStart)
                    .amount(existingBudget != null ? existingBudget.getAmount() : null)
                    .aiRecommendation(aiRecommendation)
                    .lastMonthComparison(lastMonthComparison)
                    .build();

            combinedBudgets.add(dto);
        }

        return combinedBudgets;
    }

    @Override
    @Transactional
    public BudgetDTO updateBudget(Long budgetId, CreateBudgetDTO updatedBudgetDTO, Long userId) {
        Budget existingBudget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found or not owned by user with ID: " + budgetId));

        if (updatedBudgetDTO.getMonthStart() != null &&
                !existingBudget.getMonthStart().equals(YearMonth.from(updatedBudgetDTO.getMonthStart()).atDay(1))) {
            throw new IllegalArgumentException("Cannot change budget month during update. Create a new budget for a different month.");
        }

        if (updatedBudgetDTO.getCategoryId() != null &&
                !existingBudget.getCategory().getId().equals(updatedBudgetDTO.getCategoryId())) {
            throw new IllegalArgumentException("Cannot change budget category during update. Create a new budget for a different category.");
        }

        budgetMapper.updateEntityFromDto(updatedBudgetDTO, existingBudget); // Use the new DTO for mapping
        return budgetMapper.toDto(budgetRepository.save(existingBudget));
    }

    @Override
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        if (!budgetRepository.existsByIdAndUserId(budgetId, userId)) {
            throw new EntityNotFoundException("Budget not found or not owned by user with ID: " + budgetId);
        }
        budgetRepository.deleteById(budgetId);
    }
}