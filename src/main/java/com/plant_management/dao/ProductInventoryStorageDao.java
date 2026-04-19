package com.plant_management.dao;

import com.plant_management.model.ProductInventoryStorage;
import com.plant_management.model.Products;
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
public class ProductInventoryStorageDao {

    private final JdbcTemplate jdbc;

    public ProductInventoryStorageDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<ProductInventoryStorage> STORAGE_ROW_MAPPER = (rs, rowNum) -> {
        ProductInventoryStorage storage = new ProductInventoryStorage();
        storage.setP_storage_unit_id(rs.getInt("p_storage_unit_id"));
        storage.setCapacity(rs.getFloat("capacity"));
        storage.setQuantity_stored(rs.getFloat("quantity_stored"));

        // Creating a dummy Products object to hold the foreign key ID
        // Assuming your Products class has a 'setProduct_id' method. Change if named differently!
        int productId = rs.getInt("product_id");
        if (!rs.wasNull()) {
            Products product = new Products();
            product.setProduct_id(productId);
            storage.setProducts(product);
        } else {
            storage.setProducts(null);
        }

        return storage;
    };

    public List<ProductInventoryStorage> findAll() {
        String sql = "SELECT p_storage_unit_id, capacity, quantity_stored, product_id FROM product_inventory_storage";
        return jdbc.query(sql, STORAGE_ROW_MAPPER);
    }

    public Optional<ProductInventoryStorage> findById(Integer id) {
        String sql = "SELECT p_storage_unit_id, capacity, quantity_stored, product_id FROM product_inventory_storage WHERE p_storage_unit_id = ?";
        try {
            ProductInventoryStorage storage = jdbc.queryForObject(sql, STORAGE_ROW_MAPPER, id);
            return Optional.ofNullable(storage);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public ProductInventoryStorage insert(ProductInventoryStorage storage) {
        final String sql = "INSERT INTO product_inventory_storage (capacity, quantity_stored, product_id) VALUES (?, ?, ?)";
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

            if (storage.getProducts() != null && storage.getProducts().getProduct_id() != null && storage.getProducts().getProduct_id() > 0) {
                ps.setObject(3, storage.getProducts().getProduct_id(), Types.INTEGER);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            storage.setP_storage_unit_id(key.intValue());
        }
        return storage;
    }

    public ProductInventoryStorage save(ProductInventoryStorage storage) {
        if (storage.getP_storage_unit_id() != null && storage.getP_storage_unit_id() > 0) {
            String sql = "UPDATE product_inventory_storage SET capacity = ?, quantity_stored = ?, product_id = ? WHERE p_storage_unit_id = ?";

            Object productId = (storage.getProducts() != null && storage.getProducts().getProduct_id() != null && storage.getProducts().getProduct_id() > 0)
                    ? storage.getProducts().getProduct_id() : null;

            jdbc.update(sql, storage.getCapacity(), storage.getQuantity_stored(), productId, storage.getP_storage_unit_id());
            return storage;
        } else {
            return insert(storage);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM product_inventory_storage WHERE p_storage_unit_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM product_inventory_storage WHERE p_storage_unit_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ==========================================
    // CUSTOM METHODS (Translated from JPA file)
    // ==========================================

    public List<ProductInventoryStorage> findByProducts(Products products) {
        if (products == null || products.getProduct_id() == null) {
            return List.of(); // Return empty list if product is null
        }

        String sql = "SELECT p_storage_unit_id, capacity, quantity_stored, product_id FROM product_inventory_storage WHERE product_id = ?";
        return jdbc.query(sql, STORAGE_ROW_MAPPER, products.getProduct_id());
    }
}