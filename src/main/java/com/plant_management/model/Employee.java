package com.plant_management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee {

    private Integer employee_id;

    // Replace object reference with FK
    private Integer dept_id;

    private String first_name;
    private String last_name;
    private Date date_of_birth;
    private String cnic;
    private String email;
    private String designation;
    private String address;
    private Gender gender;
    private Integer absences;
    private Integer leaves;

    public enum Gender {
        male, female
    }
}