package com.plant_management.model;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private Integer customer_id;

    private String customer_name;

    private String email;

    private String phone;

    private String address;
}