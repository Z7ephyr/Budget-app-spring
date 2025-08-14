package com.budgetapp.backend.repositories;

import com.budgetapp.backend.model.Budget;
import com.budgetapp.backend.model.User; // Keep this import for clarity if you still use it in some cases or for entity definition
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {


    List<Budget> findByUserId(Long userId);

    Optional<Budget> findByUserIdAndMonthStart(Long userId, LocalDate monthStart);


    List<Budget> findByUserIdAndMonthStartBetween(Long userId, LocalDate startDate, LocalDate endDate);

}