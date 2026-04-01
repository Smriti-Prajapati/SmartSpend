package com.smartspend.service;

import com.smartspend.dto.DashboardDTO;
import com.smartspend.model.*;
import com.smartspend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private IncomeRepository incomeRepository;
    @Autowired private UserRepository userRepository;

    public DashboardDTO getDashboard(Long userId) {
        DashboardDTO dto = new DashboardDTO();
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        Double totalIncome = incomeRepository.sumAmountByUserId(userId);
        Double totalExpenses = expenseRepository.sumAmountByUserId(userId);
        dto.setTotalIncome(totalIncome != null ? totalIncome : 0.0);
        dto.setTotalExpenses(totalExpenses != null ? totalExpenses : 0.0);
        dto.setBalance(dto.getTotalIncome() - dto.getTotalExpenses());

        Double monthlyIncome = incomeRepository.sumAmountByUserIdAndDateBetween(userId, monthStart, monthEnd);
        Double monthlyExpenses = expenseRepository.sumAmountByUserIdAndDateBetween(userId, monthStart, monthEnd);
        dto.setMonthlyIncome(monthlyIncome != null ? monthlyIncome : 0.0);
        dto.setMonthlyExpenses(monthlyExpenses != null ? monthlyExpenses : 0.0);
        dto.setMonthlyBalance(dto.getMonthlyIncome() - dto.getMonthlyExpenses());

        User user = userRepository.findById(userId).orElseThrow();
        if (user.getMonthlyBudget() != null && user.getMonthlyBudget() > 0) {
            dto.setMonthlyBudget(user.getMonthlyBudget());
            double pct = (dto.getMonthlyExpenses() / user.getMonthlyBudget()) * 100;
            dto.setBudgetUsagePercent(Math.min(pct, 100.0));
            dto.setBudgetExceeded(dto.getMonthlyExpenses() > user.getMonthlyBudget());
        }

        Map<String, Double> breakdown = new LinkedHashMap<>();
        for (Object[] row : expenseRepository.getCategoryBreakdown(userId, monthStart, monthEnd))
            breakdown.put((String) row[0], ((Number) row[1]).doubleValue());
        dto.setCategoryBreakdown(breakdown);

        LocalDate sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1);
        dto.setMonthlyTrends(buildTrends(userId, sixMonthsAgo, monthEnd));
        dto.setRecentTransactions(buildRecent(userId));
        return dto;
    }

    private List<DashboardDTO.MonthlyTrend> buildTrends(Long userId, LocalDate start, LocalDate end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        Map<String, DashboardDTO.MonthlyTrend> map = new LinkedHashMap<>();
        for (LocalDate c = start; !c.isAfter(end); c = c.plusMonths(1)) {
            String key = c.format(fmt);
            DashboardDTO.MonthlyTrend t = new DashboardDTO.MonthlyTrend();
            t.setMonth(key); t.setIncome(0.0); t.setExpense(0.0);
            map.put(key, t);
        }
        for (Object[] r : incomeRepository.getMonthlyTotals(userId, start, end)) {
            String key = LocalDate.of(((Number)r[1]).intValue(), ((Number)r[0]).intValue(), 1).format(fmt);
            if (map.containsKey(key)) map.get(key).setIncome(((Number)r[2]).doubleValue());
        }
        for (Object[] r : expenseRepository.getMonthlyTotals(userId, start, end)) {
            String key = LocalDate.of(((Number)r[1]).intValue(), ((Number)r[0]).intValue(), 1).format(fmt);
            if (map.containsKey(key)) map.get(key).setExpense(((Number)r[2]).doubleValue());
        }
        return new ArrayList<>(map.values());
    }

    private List<DashboardDTO.TransactionDTO> buildRecent(Long userId) {
        List<DashboardDTO.TransactionDTO> list = new ArrayList<>();
        for (Expense e : expenseRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId)) {
            DashboardDTO.TransactionDTO t = new DashboardDTO.TransactionDTO();
            t.setId(e.getId()); t.setTitle(e.getTitle()); t.setAmount(e.getAmount());
            t.setDate(e.getDate().toString()); t.setType("EXPENSE");
            t.setCategory(e.getCategory().getName());
            t.setCategoryColor(e.getCategory().getColor());
            t.setCategoryIcon(e.getCategory().getIcon());
            list.add(t);
        }
        for (Income i : incomeRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId)) {
            DashboardDTO.TransactionDTO t = new DashboardDTO.TransactionDTO();
            t.setId(i.getId()); t.setTitle(i.getTitle()); t.setAmount(i.getAmount());
            t.setDate(i.getDate().toString()); t.setType("INCOME");
            t.setCategory(i.getSource() != null ? i.getSource() : "Income");
            t.setCategoryColor("#10b981"); t.setCategoryIcon("💰");
            list.add(t);
        }
        list.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return list.subList(0, Math.min(10, list.size()));
    }
}
