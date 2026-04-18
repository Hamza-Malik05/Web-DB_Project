package com.plant_management.model;

import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {


    private Integer bill_id;
    private LocalDate issue_date;
    private LocalDate due_date;
    private String bill_type;
    private Transaction transaction;

}
