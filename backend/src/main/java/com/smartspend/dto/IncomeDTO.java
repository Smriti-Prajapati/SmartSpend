package com.smartspend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeDTO {
    private Long id;
    @NotBlank private String title;
    @NotNull @Positive private Double amount;
    @NotNull private LocalDate date;
    private String description;
    private String source;
}
