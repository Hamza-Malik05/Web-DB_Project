package com.plant_management.model;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductInventoryStorage {

    private Integer p_storage_unit_id;

    private Float capacity;

    private Float quantity_stored;

    private Products products;


}
