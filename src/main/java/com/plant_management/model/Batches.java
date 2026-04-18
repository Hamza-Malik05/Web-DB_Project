package com.plant_management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Batches {

    private Integer batch_id;
    private Products product;
    private Employee employee;
    private Float quantity_used;
    private Float quantity_produced;
    private String start_time;
    private String end_time;
    private RawMaterialInventoryStorage rawMaterialInventoryStorage;
    private ProductInventoryStorage productInventoryStorage;
}
