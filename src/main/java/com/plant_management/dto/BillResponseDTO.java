package com.plant_management.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillResponseDTO {

    private Integer bill_id;
    private BigDecimal amount;
    private LocalDate issue_date;
    private LocalDate due_date;
    private String bill_type;
    private String payment_method;



    // Getters & Setters (or @Data Lombok if preferred)
}
