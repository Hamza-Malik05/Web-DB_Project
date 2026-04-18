package com.plant_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private int driverId;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "license_no", nullable = false)
    private String licenseNo;
}