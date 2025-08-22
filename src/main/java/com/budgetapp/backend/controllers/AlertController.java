package com.budgetapp.backend.controllers;

import com.budgetapp.backend.dtos.alerts.AlertDTO;
import com.budgetapp.backend.config.UserDetailsImpl; // Import our custom UserDetails class
import com.budgetapp.backend.services.AlertService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import the AuthenticationPrincipal annotation
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<?> createAlert(
            @Valid @RequestBody AlertDTO alertDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId();
            AlertDTO createdAlert = alertService.createAlert(alertDTO, userId);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating alert: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        List<AlertDTO> alerts = alertService.getAlertsByUserId(userId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }


    @GetMapping("/unread")
    public ResponseEntity<List<AlertDTO>> getUnreadAlerts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        List<AlertDTO> unreadAlerts = alertService.getUnreadAlertsByUserId(userId);
        return new ResponseEntity<>(unreadAlerts, HttpStatus.OK);
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAlertAsRead(
            @PathVariable("id") Long alertId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId();
            AlertDTO updatedAlert = alertService.markAlertAsRead(alertId, userId);
            return new ResponseEntity<>(updatedAlert, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error marking alert as read: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(
            @PathVariable("id") Long alertId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getId();
            alertService.deleteAlert(alertId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
