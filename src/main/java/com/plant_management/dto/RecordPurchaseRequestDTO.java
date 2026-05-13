package com.plant_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordPurchaseRequestDTO {
    private Integer supplier_id;
    private BigDecimal units_bought;
}