package com.plant_management.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RawMaterialInventoryStorage {


    private Integer r_storage_unit_id;

    private Float capacity;

    private Float quantity_stored;

}
