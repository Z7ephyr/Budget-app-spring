package com.budgetapp.backend.dtos.insights;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Data
@Builder
public class BudgetInsightDTO {
    private String category;
    private BigDecimal currentLimit;
    private BigDecimal recommendedLimit;
    private String explanation;
}

