package com.plant_management.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Integer user_id;
    private Employee employee;
    private String username;
    private String password;
    private Role role;

    public enum Role {
        hr_manager, warehouse_manager
        ,production_supervisor, finance_manager,
        sales_manager,admin
    }
}
