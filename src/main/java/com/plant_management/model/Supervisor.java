package com.plant_management.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supervisor {

    private int supervisor_id;
    private Employee employee;
    private String officeNo;
}