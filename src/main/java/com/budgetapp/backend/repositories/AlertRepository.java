package com.budgetapp.backend.repositories;

import com.budgetapp.backend.model.Alert;
import com.budgetapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

;


    List<Alert> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Alert> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);


}