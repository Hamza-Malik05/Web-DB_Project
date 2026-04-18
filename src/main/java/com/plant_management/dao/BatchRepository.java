package com.plant_management.dao;

import com.plant_management.model.Batches;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batches, Integer> {
    @EntityGraph(attributePaths = {"product", "employee"})
    List<Batches> findAll();
}