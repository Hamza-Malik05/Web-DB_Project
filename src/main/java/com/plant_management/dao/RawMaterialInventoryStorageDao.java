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
        storage.setId(rs.getInt("id"));

        // Using getObject to safely handle potential database NULLs mapped to Java Float objects
        storage.setCapacity(rs.getObject("capacity", Float.class));
        storage.setQuantity_stored(rs.getObject("quantity_stored", Float.class));

        return storage;
    };

    public List<RawMaterialInventoryStorage> findAll() {
        String sql = "SELECT id, capacity, quantity_stored FROM raw_material_inventory_storage";
        return jdbc.query(sql, STORAGE_ROW_MAPPER);
    }

    public Optional<RawMaterialInventoryStorage> findById(Integer id) {
        String sql = "SELECT id, capacity, quantity_stored FROM raw_material_inventory_storage WHERE id = ?";
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
            storage.setId(key.intValue());
        }
        return storage;
    }

    public RawMaterialInventoryStorage save(RawMaterialInventoryStorage storage) {
        // If it already has an ID, update it. Otherwise, insert it.
        if (storage.getId() != null && storage.getId() > 0) {
            String sql = "UPDATE raw_material_inventory_storage SET capacity = ?, quantity_stored = ? WHERE id = ?";
            jdbc.update(sql, storage.getCapacity(), storage.getQuantity_stored(), storage.getId());
            return storage;
        } else {
            return insert(storage);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM raw_material_inventory_storage WHERE id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM raw_material_inventory_storage WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}