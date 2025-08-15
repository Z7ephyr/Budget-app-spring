package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.budgets.BudgetDTO;
import com.budgetapp.backend.dtos.budgets.BudgetWithRecommendationDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final UserRepository userRepository;
    private final RecommendationService recommendationService;

    public BudgetServiceImpl(BudgetRepository budgetRepository, BudgetMapper budgetMapper, UserRepository userRepository, RecommendationService recommendationService) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
        this.userRepository = userRepository;
        this.recommendationService = recommendationService;
    }

    @Override
    @Transactional
    public BudgetDTO createBudget(BudgetDTO budgetDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        LocalDate monthStart = budgetDTO.getMonthStart();
        if (monthStart == null) {
            monthStart = YearMonth.now().atDay(1);
            budgetDTO.setMonthStart(monthStart);
        } else {
            monthStart = YearMonth.from(monthStart).atDay(1);
            budgetDTO.setMonthStart(monthStart);
        }

        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndMonthStartAndCategoryId(
                userId, monthStart, budgetDTO.getCategoryId());

        if (existingBudget.isPresent()) {
            throw new IllegalArgumentException("A budget already exists for this user, month, and category: " + budgetDTO.getCategoryId());
        }

        Budget budget = budgetMapper.toEntity(budgetDTO);
        budget.setUser(user);
        Budget savedBudget = budgetRepository.save(budget);

        return budgetMapper.toDto(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetDTO> getBudgetById(Long budgetId, Long userId) {
        return budgetRepository.findByIdAndUserId(budgetId, userId)
                .map(budgetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetDTO> getBudgetByUserIdAndMonth(Long userId, YearMonth month) {
        // This method is now ambiguous. For the purpose of this exercise,
        // we'll return the first one found, but getAllBudgetsByUserId is
        // the recommended approach to get all budgets for a month.
        LocalDate monthStart = month.atDay(1);
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthStart(userId, monthStart);
        return budgets.stream().map(budgetMapper::toDto).findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetWithRecommendationDTO> getAllBudgetsByUserId(Long userId) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        List<Budget> userBudgets = budgetRepository.findByUserIdAndMonthStart(userId, monthStart);
        Map<Long, Map<String, BigDecimal>> recommendations = recommendationService.calculateBudgetRecommendations(userId);

        List<BudgetWithRecommendationDTO> combinedBudgets = new ArrayList<>();
        for (Budget budget : userBudgets) {
            Map<String, BigDecimal> recommendationData = recommendations.get(budget.getCategoryId());
            BigDecimal aiRecommendation = BigDecimal.ZERO;
            BigDecimal lastMonthComparison = BigDecimal.ZERO;

            if (recommendationData != null) {
                aiRecommendation = recommendationData.get("aiRecommendation");
                lastMonthComparison = recommendationData.get("lastMonthComparison");
            }

            BudgetWithRecommendationDTO combinedDto = BudgetWithRecommendationDTO.builder()
                    .id(budget.getId())
                    .categoryId(budget.getCategoryId())
                    .monthStart(budget.getMonthStart())
                    .amount(budget.getAmount())
                    .aiRecommendation(aiRecommendation)
                    .lastMonthComparison(lastMonthComparison)
                    .build();

            combinedBudgets.add(combinedDto);
        }

        return combinedBudgets;
    }

    @Override
    @Transactional
    public BudgetDTO updateBudget(Long budgetId, BudgetDTO updatedBudgetDTO, Long userId) {
        Budget existingBudget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found or not owned by user with ID: " + budgetId));

        if (updatedBudgetDTO.getMonthStart() != null && !existingBudget.getMonthStart().equals(YearMonth.from(updatedBudgetDTO.getMonthStart()).atDay(1))) {
            throw new IllegalArgumentException("Cannot change budget month during update. Create a new budget for a different month.");
        }
        if (updatedBudgetDTO.getCategoryId() != null && !existingBudget.getCategoryId().equals(updatedBudgetDTO.getCategoryId())) {
            throw new IllegalArgumentException("Cannot change budget category during update. Create a new budget for a different category.");
        }

        budgetMapper.updateEntityFromDto(updatedBudgetDTO, existingBudget);
        Budget savedBudget = budgetRepository.save(existingBudget);
        return budgetMapper.toDto(savedBudget);
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
