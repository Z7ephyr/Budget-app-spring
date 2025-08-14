package com.budgetapp.backend.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @RequestBody and @Valid annotations.
     * Returns a 400 Bad Request with a map of field errors.
     * @param ex The MethodArgumentNotValidException thrown.
     * @return A ResponseEntity containing a map of field-specific errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles validation errors from method-level validation.
     * Returns a 400 Bad Request with a map of constraint violations.
     * @param ex The ConstraintViolationException thrown.
     * @return A ResponseEntity containing a map of field-specific errors.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString().substring(violation.getPropertyPath().toString().lastIndexOf(".") + 1);
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles DataIntegrityViolationException, typically from unique constraints on database fields.
     * Returns a 409 Conflict with a structured JSON error message.
     * @param ex The DataIntegrityViolationException thrown.
     * @return A ResponseEntity containing a user-friendly JSON error message.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        // In a real application, you might parse the error message more carefully.
        // For now, this generic approach ensures a valid JSON response.
        errors.put("email", "This email is already registered.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    /**
     * A fallback handler for any other unexpected exceptions.
     * This ensures that any uncaught exception will return a JSON error instead of a generic HTML page.
     * @param ex The unexpected exception.
     * @return A ResponseEntity with a 500 Internal Server Error and a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("general", "An unexpected error occurred. Please try again later.");
        // Log the full stack trace to the console for debugging
        System.err.println("Unexpected exception: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
