package com.plant_management.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Products {

    private Integer product_id;

    private String name;
    private String unit_of_measurement;
    private BigDecimal price_per_unit;

}