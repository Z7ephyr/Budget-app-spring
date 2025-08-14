package com.budgetapp.backend.services;

import com.budgetapp.backend.dtos.alerts.AlertDTO;
import java.util.List;

public interface AlertService {

    // Method to create a new alert (e.g., triggered by overspending)
    AlertDTO createAlert(AlertDTO alertDTO, Long userId);

    // Method to fetch all alerts for a specific user
    List<AlertDTO> getAlertsByUserId(Long userId);

    // Method to fetch unread alerts for a specific user (from Alerts screen description)
    List<AlertDTO> getUnreadAlertsByUserId(Long userId);

    // Method to mark a specific alert as read
    AlertDTO markAlertAsRead(Long alertId, Long userId); // Ensure user owns the alert

    // Method to delete an alert (if your app allows it)
    void deleteAlert(Long alertId, Long userId); // Ensure user owns the alert


}