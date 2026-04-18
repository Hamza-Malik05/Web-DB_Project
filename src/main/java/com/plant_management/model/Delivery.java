package com.plant_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer delivery_id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "driver_id")
    private Driver driver;

    @Column(name = "departure_time")  // maps to DB
    private LocalDateTime departureTime;  // Java naming

    @Column(name = "delivery_time")  // maps to DB
    private LocalDateTime deliveryTime;  // Java naming
}