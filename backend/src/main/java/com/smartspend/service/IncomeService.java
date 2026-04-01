package com.smartspend.service;

import com.smartspend.dto.IncomeDTO;
import com.smartspend.model.*;
import com.smartspend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    @Autowired private IncomeRepository incomeRepository;
    @Autowired private UserRepository userRepository;

    public IncomeDTO create(Long userId, IncomeDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Income i = new Income();
        i.setTitle(dto.getTitle()); i.setAmount(dto.getAmount());
        i.setDate(dto.getDate()); i.setDescription(dto.getDescription());
        i.setSource(dto.getSource()); i.setUser(user);
        return toDTO(incomeRepository.save(i));
    }

    public IncomeDTO update(Long userId, Long id, IncomeDTO dto) {
        Income i = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found"));
        if (!i.getUser().getId().equals(userId)) throw new RuntimeException("Unauthorized");
        i.setTitle(dto.getTitle()); i.setAmount(dto.getAmount());
        i.setDate(dto.getDate()); i.setDescription(dto.getDescription());
        i.setSource(dto.getSource());
        return toDTO(incomeRepository.save(i));
    }

    public void delete(Long userId, Long id) {
        Income i = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found"));
        if (!i.getUser().getId().equals(userId)) throw new RuntimeException("Unauthorized");
        incomeRepository.delete(i);
    }

    public List<IncomeDTO> getAll(Long userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<IncomeDTO> getByDateRange(Long userId, LocalDate start, LocalDate end) {
        return incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public IncomeDTO toDTO(Income i) {
        IncomeDTO dto = new IncomeDTO();
        dto.setId(i.getId()); dto.setTitle(i.getTitle());
        dto.setAmount(i.getAmount()); dto.setDate(i.getDate());
        dto.setDescription(i.getDescription()); dto.setSource(i.getSource());
        return dto;
    }
}
