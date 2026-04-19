package com.plant_management.model;

import lombok.*;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {


    private Integer bill_id;
    private Date issue_date;
    private Date due_date;
    private String bill_type;
    private Transaction transaction;

}
