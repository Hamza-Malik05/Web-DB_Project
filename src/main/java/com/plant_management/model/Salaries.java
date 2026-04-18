package com.plant_management.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salaries {

    private Integer salaryId;

    private Transaction transaction;

    private Employee employee;

    private BigDecimal bonus;

    private BigDecimal fine;
}