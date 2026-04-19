package com.plant_management.dao;

import com.plant_management.model.Products;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDao {

    private final JdbcTemplate jdbc;

    public ProductDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Maps the database row directly to your Products model
    private final RowMapper<Products> PRODUCT_ROW_MAPPER = (rs, rowNum) -> {
        Products p = new Products();
        p.setProduct_id(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setUnit_of_measurement(rs.getString("unit_of_measurement"));
        p.setPrice_per_unit(rs.getBigDecimal("price_per_unit"));
        return p;
    };

    public List<Products> findAll() {
        String sql = "SELECT product_id, name, unit_of_measurement, price_per_unit FROM products";
        return jdbc.query(sql, PRODUCT_ROW_MAPPER);
    }

    public Optional<Products> findById(Integer id) {
        String sql = "SELECT product_id, name, unit_of_measurement, price_per_unit FROM products WHERE product_id = ?";
        try {
            Products p = jdbc.queryForObject(sql, PRODUCT_ROW_MAPPER, id);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Products insert(Products product) {
        final String sql = "INSERT INTO products (name, unit_of_measurement, price_per_unit) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setString(2, product.getUnit_of_measurement());
            ps.setBigDecimal(3, product.getPrice_per_unit());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            product.setProduct_id(key.intValue());
        }
        return product;
    }

    public Products save(Products product) {
        // If it already has an ID, update it. Otherwise, insert it.
        if (product.getProduct_id() != null && product.getProduct_id() > 0) {
            String sql = "UPDATE products SET name = ?, unit_of_measurement = ?, price_per_unit = ? WHERE product_id = ?";
            jdbc.update(sql, product.getName(), product.getUnit_of_measurement(), product.getPrice_per_unit(), product.getProduct_id());
            return product;
        } else {
            return insert(product);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}