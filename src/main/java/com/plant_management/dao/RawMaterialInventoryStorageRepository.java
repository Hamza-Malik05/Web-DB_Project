package com.plant_management.dao;

import com.plant_management.model.RawMaterialInventoryStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMaterialInventoryStorageRepository extends JpaRepository<RawMaterialInventoryStorage, Integer> {
}
