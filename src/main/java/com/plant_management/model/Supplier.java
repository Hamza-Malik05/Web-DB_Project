package com.plant_management.model;


import lombok.Data;

@Data

public class Supplier {

    private Integer supplier_id;

    private String name;

    private String email;

    private String phone;

    private String address;

    private String city;
}