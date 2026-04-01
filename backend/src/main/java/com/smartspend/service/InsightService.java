package com.smartspend.service;

import com.smartspend.dto.InsightDTO;
import com.smartspend.model.Expense;
import com.smartspend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class InsightService {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private ExpenseService expenseService;

    public InsightDTO getInsights(Long userId) {
        InsightDTO dto = new InsightDTO();
        LocalDate now = LocalDate.now();
        LocalDate curStart = now.withDayOfMonth(1);
        LocalDate curEnd = now.withDayOfMonth(now.lengthOfMonth());
        LocalDate prevStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate prevEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        Double cur = expenseRepository.sumAmountByUserIdAndDateBetween(userId, curStart, curEnd);
        Double prev = expenseRepository.sumAmountByUserIdAndDateBetween(userId, prevStart, prevEnd);
        cur = cur != null ? cur : 0.0;
        prev = prev != null ? prev : 0.0;
        dto.setCurrentMonthExpenses(cur);
        dto.setPreviousMonthExpenses(prev);

        if (prev > 0) {
            double change = ((cur - prev) / prev) * 100;
            dto.setChangePercent(Math.abs(change));
            dto.setChangeDirection(change >= 0 ? "UP" : "DOWN");
        } else {
            dto.setChangePercent(0.0);
            dto.setChangeDirection("NEUTRAL");
        }

        List<InsightDTO.CategorySpend> cats = new ArrayList<>();
        for (Object[] row : expenseRepository.getTopCategoriesWithDetails(userId, curStart, curEnd)) {
            InsightDTO.CategorySpend cs = new InsightDTO.CategorySpend();
            cs.setCategory((String) row[0]); cs.setColor((String) row[1]); cs.setIcon((String) row[2]);
            double amt = ((Number) row[3]).doubleValue();
            cs.setAmount(amt);
            cs.setPercentage(cur > 0 ? (amt / cur) * 100 : 0);
            cats.add(cs);
        }
        dto.setTopCategories(cats);

        dto.setDailyAverage(cur / Math.max(1, now.getDayOfMonth()));

        expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, curStart, curEnd)
                .stream().max(Comparator.comparingDouble(Expense::getAmount))
                .ifPresent(e -> dto.setHighestExpense(expenseService.toDTO(e)));

        List<String> alerts = new ArrayList<>();
        for (InsightDTO.CategorySpend cs : cats)
            if (cs.getPercentage() > 40)
                alerts.add(cs.getCategory() + " is " + String.format("%.0f", cs.getPercentage()) + "% of your spending.");
        if ("UP".equals(dto.getChangeDirection()) && dto.getChangePercent() > 20)
            alerts.add("Spending up " + String.format("%.0f", dto.getChangePercent()) + "% vs last month.");
        dto.setOverspendingAlerts(alerts);
        dto.setSuggestions(generateSuggestions(cats, dto.getChangeDirection(), dto.getChangePercent()));
        dto.setCategoryTrends(buildCategoryTrends(userId, now));
        return dto;
    }

    private List<String> generateSuggestions(List<InsightDTO.CategorySpend> cats, String dir, Double pct) {
        List<String> s = new ArrayList<>();
        for (InsightDTO.CategorySpend cs : cats) {
            switch (cs.getCategory().toLowerCase()) {
                case "food & dining" -> { if (cs.getPercentage() > 30) s.add("Try meal prepping to cut Food costs by up to 30%."); }
                case "travel"        -> { if (cs.getPercentage() > 20) s.add("Consider carpooling or public transport to reduce Travel costs."); }
                case "shopping"      -> { if (cs.getPercentage() > 25) s.add("Use a wishlist to avoid impulse Shopping purchases."); }
                case "entertainment" -> { if (cs.getPercentage() > 15) s.add("Look for free or discounted Entertainment options."); }
                case "bills & utilities" -> { if (cs.getPercentage() > 35) s.add("Review subscriptions — you may be paying for unused services."); }
            }
        }
        if ("UP".equals(dir) && pct > 15) s.add("Spending rose significantly. Consider setting a stricter monthly budget.");
        if (s.isEmpty()) s.add("Great job! Your spending looks balanced this month.");
        return s;
    }

    private Map<String, List<Double>> buildCategoryTrends(Long userId, LocalDate now) {
        Map<String, List<Double>> trends = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate m = now.minusMonths(i);
            LocalDate start = m.withDayOfMonth(1);
            LocalDate end = m.withDayOfMonth(m.lengthOfMonth());
            for (Object[] row : expenseRepository.getTopCategoriesWithDetails(userId, start, end)) {
                String cat = (String) row[0];
                double amt = ((Number) row[3]).doubleValue();
                trends.computeIfAbsent(cat, k -> new ArrayList<>(Collections.nCopies(6, 0.0)));
                trends.get(cat).set(5 - i, amt);
            }
        }
        return trends;
    }
}
