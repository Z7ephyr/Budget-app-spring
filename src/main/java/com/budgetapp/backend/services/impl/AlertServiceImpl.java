package com.budgetapp.backend.services.impl;

import com.budgetapp.backend.dtos.alerts.AlertDTO;
import com.budgetapp.backend.mappers.AlertMapper;
import com.budgetapp.backend.model.Alert;
import com.budgetapp.backend.model.User;
import com.budgetapp.backend.repositories.AlertRepository;
import com.budgetapp.backend.repositories.UserRepository;
import com.budgetapp.backend.services.AlertService; // This import should now resolve
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import for @Transactional

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertServiceImpl implements AlertService { // 'implements AlertService' should now be valid

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;
    private final UserRepository userRepository;

    public AlertServiceImpl(AlertRepository alertRepository, AlertMapper alertMapper, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.alertMapper = alertMapper;
        this.userRepository = userRepository;
    }

    @Override // Add @Override as it's an interface method implementation
    @Transactional
    public AlertDTO createAlert(AlertDTO alertDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Alert alert = alertMapper.toEntity(alertDTO);
        alert.setUser(user);
        alert.setRead(false); // New alerts are typically unread by default

        Alert savedAlert = alertRepository.save(alert);
        return alertMapper.toDto(savedAlert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertDTO> getAlertsByUserId(Long userId) {
        // You might want to sort these, e.g., by createdAt descending
        List<Alert> alerts = alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return alerts.stream()
                .map(alertMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertDTO> getUnreadAlertsByUserId(Long userId) {
        List<Alert> unreadAlerts = alertRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        return unreadAlerts.stream()
                .map(alertMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlertDTO markAlertAsRead(Long alertId, Long userId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found with ID: " + alertId));

        // Security check: ensure the alert belongs to the requesting user
        if (!alert.getUser().getId().equals(userId)) {
            throw new SecurityException("Access Denied: Alert does not belong to user ID: " + userId);
        }

        alert.setRead(true); // Mark as read
        Alert updatedAlert = alertRepository.save(alert);
        return alertMapper.toDto(updatedAlert);
    }

    @Override
    @Transactional
    public void deleteAlert(Long alertId, Long userId) {
        Alert alertToDelete = alertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found with ID: " + alertId));

        // Security check: ensure the alert belongs to the requesting user
        if (!alertToDelete.getUser().getId().equals(userId)) {
            throw new SecurityException("Access Denied: Alert does not belong to user ID: " + userId);
        }

        alertRepository.delete(alertToDelete);
    }
}