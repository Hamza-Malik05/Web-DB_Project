package com.plant_management.model;


import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {


    private Integer vehicle_id;

    private VehicleType type;

    private String license_plate;

    private String model;

    private Float capacity;

    private Status status;

    public enum Status {
        active, inactive
    }


    // The Enum definition
    public enum VehicleType {
        truck,
        van,
        loader,
        bike,
        other
    }
}