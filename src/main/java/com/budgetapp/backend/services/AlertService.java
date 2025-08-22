package com.budgetapp.backend.services;

import com.budgetapp.backend.dtos.alerts.AlertDTO;
import java.util.List;

public interface AlertService {


    AlertDTO createAlert(AlertDTO alertDTO, Long userId);


    List<AlertDTO> getAlertsByUserId(Long userId);


    List<AlertDTO> getUnreadAlertsByUserId(Long userId);


    AlertDTO markAlertAsRead(Long alertId, Long userId);


    void deleteAlert(Long alertId, Long userId);


}