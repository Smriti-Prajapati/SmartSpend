package com.smartspend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {
    private Double totalIncome;
    private Double totalExpenses;
    private Double balance;
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Double monthlyBalance;
    private Map<String, Double> categoryBreakdown;
    private List<MonthlyTrend> monthlyTrends;
    private List<TransactionDTO> recentTransactions;
    private Double monthlyBudget;
    private Boolean budgetExceeded;
    private Double budgetUsagePercent;

    @Data
    public static class MonthlyTrend {
        private String month;
        private Double income;
        private Double expense;
    }

    @Data
    public static class TransactionDTO {
        private Long id;
        private String title;
        private Double amount;
        private String date;
        private String type;
        private String category;
        private String categoryColor;
        private String categoryIcon;
    }
}
