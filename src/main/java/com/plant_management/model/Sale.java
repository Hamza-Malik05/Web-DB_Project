package com.plant_management.model;


import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    private int sale_id;

    private int transaction_id;

    private Transaction transaction;

    private int order_id;

    private float units_sold;

    private String status;
}
