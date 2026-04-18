package com.plant_management.model;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrdersProducts {


    private Integer order_id;

    private Integer product_id;

    private Float quantity;
}