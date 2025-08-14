package com.budgetapp.backend.dtos.insights;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpendingTrendDTO {
    private String category;
    private Map<YearMonth, BigDecimal> monthlyTotals;
}