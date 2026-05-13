package com.plant_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponseDTO {
    private Integer purchase_id;
    private String supplier_name;
    private LocalDate date_of_purchase;
    private LocalDate delivery_date;
    private String unit_of_measurement;
    private BigDecimal units_bought;
    private BigDecimal price_per_unit;
    private BigDecimal total_bill;
}