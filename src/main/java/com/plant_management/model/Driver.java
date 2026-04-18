package com.plant_management.model;


import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {


    private int driverId;

    private Employee employee;

    private String licenseNo;
}