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

    // The attribute in your Order class
    private OrderStatus status;


    private String address;

    // The Enum definition
    public enum OrderStatus {
        pending,
        processing,
        delivered,
        cancelled,
        shipped
    }
}