package com.plant_management.dao;

import com.plant_management.model.RawMaterialInventoryStorage;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class RawMaterialInventoryStorageDao {

    private final JdbcTemplate jdbc;

    public RawMaterialInventoryStorageDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Maps the database row directly to your RawMaterialInventoryStorage model
    private final RowMapper<RawMaterialInventoryStorage> STORAGE_ROW_MAPPER = (rs, rowNum) -> {
        RawMaterialInventoryStorage storage = new RawMaterialInventoryStorage();
        storage.setR_storage_unit_id(rs.getInt("r_storage_unit_id"));

        java.math.BigDecimal capacityBd = rs.getBigDecimal("capacity");
        if (capacityBd != null) {
            storage.setCapacity(capacityBd.floatValue());
        } else {
            storage.setCapacity(null);
        }

// Safely extract quantity_stored
        java.math.BigDecimal quantityBd = rs.getBigDecimal("quantity_stored");
        if (quantityBd != null) {
            storage.setQuantity_stored(quantityBd.floatValue());
        } else {
            storage.setQuantity_stored(null);
        }

        return storage;
    };

    public List<RawMaterialInventoryStorage> findAll() {
        String sql = "SELECT r_storage_unit_id, capacity, quantity_stored FROM raw_material_inventory_storage";
        return jdbc.query(sql, STORAGE_ROW_MAPPER);
    }

    public Optional<RawMaterialInventoryStorage> findById(Integer id) {
        String sql = "SELECT r_storage_unit_id, capacity, quantity_stored FROM raw_material_inventory_storage WHERE r_storage_unit_id = ?";
        try {
            RawMaterialInventoryStorage storage = jdbc.queryForObject(sql, STORAGE_ROW_MAPPER, id);
            return Optional.ofNullable(storage);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public RawMaterialInventoryStorage insert(RawMaterialInventoryStorage storage) {
        final String sql = "INSERT INTO raw_material_inventory_storage (capacity, quantity_stored) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (storage.getCapacity() != null) {
                ps.setFloat(1, storage.getCapacity());
            } else {
                ps.setNull(1, Types.FLOAT);
            }

            if (storage.getQuantity_stored() != null) {
                ps.setFloat(2, storage.getQuantity_stored());
            } else {
                ps.setNull(2, Types.FLOAT);
            }

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            storage.setR_storage_unit_id(key.intValue());
        }
        return storage;
    }

    public RawMaterialInventoryStorage save(RawMaterialInventoryStorage storage) {
        // If it already has an ID, update it. Otherwise, insert it.
        if (storage.getR_storage_unit_id() != null && storage.getR_storage_unit_id() > 0) {
            String sql = "UPDATE raw_material_inventory_storage SET capacity = ?, quantity_stored = ? WHERE r_storage_unit_id = ?";
            jdbc.update(sql, storage.getCapacity(), storage.getQuantity_stored(), storage.getR_storage_unit_id());
            return storage;
        } else {
            return insert(storage);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM raw_material_inventory_storage WHERE r_storage_unit_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM raw_material_inventory_storage WHERE r_storage_unit_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}