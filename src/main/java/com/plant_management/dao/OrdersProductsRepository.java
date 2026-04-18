package com.plant_management.dao;

import com.plant_management.model.OrdersProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersProductsRepository extends JpaRepository<OrdersProducts, Integer> {
}