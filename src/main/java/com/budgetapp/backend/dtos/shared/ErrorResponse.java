package com.budgetapp.backend.dtos.shared;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    @NotBlank
    private String error;

    @NotBlank
    private String details;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}