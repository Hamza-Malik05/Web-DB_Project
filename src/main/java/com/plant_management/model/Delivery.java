package com.plant_management.model;


import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {


    private Integer delivery_id;

    private Order order;

    private Vehicle vehicle;

    private Driver driver;

    private LocalDateTime departureTime;  // Java naming

    private LocalDateTime deliveryTime;  // Java naming
}