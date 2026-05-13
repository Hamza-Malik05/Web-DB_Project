package com.plant_management.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    private Integer attendance_id;
    private Employee employee;
    private LocalDate date;
    private LocalTime clock_in;
    private LocalTime clock_out;
    private Status status;

    public enum Status {
        present, absent, late
    }
}