package com.plant_management.dao;

import com.plant_management.model.ProductInventoryStorage;
import com.plant_management.model.Products;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
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

        BigDecimal cap = rs.getBigDecimal("capacity");
        storage.setCapacity(cap != null ? cap.floatValue() : 0.0f);

        BigDecimal stored = rs.getBigDecimal("quantity_stored");
        storage.setQuantity_stored(stored != null ? stored.floatValue() : 0.0f);

        // ==========================
        // Map Nested Products Object
        // ==========================
        int productId = rs.getInt("product_id");
        if (!rs.wasNull()) {
            Products product = new Products();
            product.setProduct_id(productId);
            product.setName(rs.getString("product_name"));
            product.setUnit_of_measurement(rs.getString("unit_of_measurement"));

            BigDecimal price = rs.getBigDecimal("price_per_unit");
            // Assuming price is a float/double in your Java model.
            // If it's a BigDecimal in your model, change to: product.setPrice_per_unit(price);
            product.setPrice_per_unit(price != null ? price : BigDecimal.valueOf(0.0));

            storage.setProducts(product);
        } else {
            storage.setProducts(null);
        }

        return storage;
    };

    public List<ProductInventoryStorage> findAll() {
        String sql = "SELECT * FROM v_product_inventory_storage";
        return jdbc.query(sql, STORAGE_ROW_MAPPER);
    }

    public Optional<ProductInventoryStorage> findById(Integer id) {
        String sql = "SELECT * FROM v_product_inventory_storage WHERE p_storage_unit_id = ?";
        try {
            ProductInventoryStorage storage = jdbc.queryForObject(sql, STORAGE_ROW_MAPPER, id);
            return Optional.ofNullable(storage);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public ProductInventoryStorage insert(ProductInventoryStorage storage) {
        // Inserts still go to the base table
        final String sql = "INSERT INTO product_inventory_storage (capacity, quantity_stored, product_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"p_storage_unit_id"});

            if (storage.getCapacity() != null) {
                ps.setFloat(1, storage.getCapacity());
            } else {
                ps.setNull(1, Types.NUMERIC);
            }

            if (storage.getQuantity_stored() != null) {
                ps.setFloat(2, storage.getQuantity_stored());
            } else {
                ps.setNull(2, Types.NUMERIC);
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
            // Updates still go to the base table
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

    public List<ProductInventoryStorage> findByProducts(Products products) {
        if (products == null || products.getProduct_id() == null) {
            return List.of();
        }
        String sql = "SELECT * FROM v_product_inventory_storage WHERE product_id = ?";
        return jdbc.query(sql, STORAGE_ROW_MAPPER, products.getProduct_id());
    }
}