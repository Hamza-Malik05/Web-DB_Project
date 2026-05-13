package com.plant_management.model;

import lombok.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salaries {

    @JsonProperty("salary_id") // Forces JSON to use salary_id
    private Integer salaryId;

    private Transaction transaction;

    private Employee employee;

    private BigDecimal bonus;

    private BigDecimal fine;
}