package com.plant_management.dao;

import com.plant_management.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT MAX(o.order_id) FROM Order o")
    Integer findMaxOrderId();

    List<Order> findByStatus(String status);
}
