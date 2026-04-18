package com.plant_management.model;


import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {


    private Integer order_id;

    private Integer customer_id;

    private Integer employee_id;

    private LocalDate order_date;

    private String status;

    private String address;
}