package com.plant_management.model;

import lombok.*;

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
    private Float units_bought;
    private Float price_per_unit = 30.0f;
}