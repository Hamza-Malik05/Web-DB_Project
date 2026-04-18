package com.plant_management.dao;

import com.plant_management.model.Accountant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountantRepository extends JpaRepository<Accountant, Integer> {
}