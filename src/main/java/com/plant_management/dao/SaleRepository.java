package com.plant_management.dao;

import com.plant_management.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
}
