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
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // Get user details from the JWT
        try {
            Long userId = userDetails.getId(); // Securely get the user's ID
            AlertDTO createdAlert = alertService.createAlert(alertDTO, userId);
            return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating alert: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Get All Alerts for a User ---
    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // Get user details from the JWT
        Long userId = userDetails.getId(); // Securely get the user's ID
        List<AlertDTO> alerts = alertService.getAlertsByUserId(userId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    // --- Get Unread Alerts for a User ---
    @GetMapping("/unread")
    public ResponseEntity<List<AlertDTO>> getUnreadAlerts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // Get user details from the JWT
        Long userId = userDetails.getId(); // Securely get the user's ID
        List<AlertDTO> unreadAlerts = alertService.getUnreadAlertsByUserId(userId);
        return new ResponseEntity<>(unreadAlerts, HttpStatus.OK);
    }

    // --- Mark Alert as Read ---
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAlertAsRead(
            @PathVariable("id") Long alertId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // Get user details from the JWT
        try {
            Long userId = userDetails.getId(); // Securely get the user's ID
            AlertDTO updatedAlert = alertService.markAlertAsRead(alertId, userId);
            return new ResponseEntity<>(updatedAlert, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN); // 403 Forbidden (if alert not owned by user)
        } catch (Exception e) {
            return new ResponseEntity<>("Error marking alert as read: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Delete Alert ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(
            @PathVariable("id") Long alertId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // Get user details from the JWT
        try {
            Long userId = userDetails.getId(); // Securely get the user's ID
            alertService.deleteAlert(alertId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
