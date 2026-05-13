package com.plant_management.model;


import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {


    private Integer driver_id;

    private Employee employee;

    private String licenseNo;
}