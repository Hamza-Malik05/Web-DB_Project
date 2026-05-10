package com.plant_management.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Purchase {

    private Integer purchase_id;

    private Supplier supplier;
    private LocalDate date_of_purchase;
    private LocalDate delivery_date;
    private String unit_of_measurement;
    private BigDecimal units_bought;
    private BigDecimal price_per_unit = BigDecimal.valueOf(30.0);
}