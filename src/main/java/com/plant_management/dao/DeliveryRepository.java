package com.plant_management.dao;

import com.plant_management.model.Delivery;
import com.plant_management.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    List<Delivery> findByDeliveryTimeIsNull();

    List<Delivery> findByDeliveryTimeIsNotNull();

    boolean existsByOrder(Order order);
}