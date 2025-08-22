package com.budgetapp.backend.dtos.expenses;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- Add this import
import lombok.AllArgsConstructor; // <--- Add this import
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {

    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;


    @NotNull(message = "Category ID is required")
    private Long categoryId;


    private String categoryName;


    @Size(max = 255, message = "Description must be <= 255 characters")
    private String description;


    @NotNull(message = "Date is required")
    private LocalDate date;


    private String categoryDescription;


    private Long userId;
}