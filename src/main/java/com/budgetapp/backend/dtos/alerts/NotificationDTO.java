package com.budgetapp.backend.dtos.alerts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDTO {
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotBlank
    private String action;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private Long userId;
}