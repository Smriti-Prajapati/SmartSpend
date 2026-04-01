package com.smartspend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class InsightDTO {
    private List<CategorySpend> topCategories;
    private Double currentMonthExpenses;
    private Double previousMonthExpenses;
    private Double changePercent;
    private String changeDirection;
    private List<String> overspendingAlerts;
    private List<String> suggestions;
    private Double dailyAverage;
    private ExpenseDTO highestExpense;
    private Map<String, List<Double>> categoryTrends;

    @Data
    public static class CategorySpend {
        private String category;
        private Double amount;
        private Double percentage;
        private String color;
        private String icon;
    }
}
