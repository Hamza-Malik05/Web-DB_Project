package com.plant_management.model;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accountant {


    private int accountant_id;
    private Employee employee;
    private String domain;
}