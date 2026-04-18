package com.plant_management.dao;

import com.plant_management.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Override
    List<Products> findAll();
}