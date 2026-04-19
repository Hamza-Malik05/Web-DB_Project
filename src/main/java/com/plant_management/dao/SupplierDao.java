package com.plant_management.dao;

import com.plant_management.model.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class SupplierDao {

    private final JdbcTemplate jdbc;

    public SupplierDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Supplier> ROW_MAPPER = (rs, rowNum) -> {
        Supplier s = new Supplier();
        s.setSupplier_id(rs.getObject("supplier_id") != null ? rs.getInt("supplier_id") : null);
        s.setName(rs.getString("name"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        return s;
    };

    public List<Supplier> findAll() {
        String sql = "SELECT supplier_id, name, phone, email, address FROM supplier";
        return jdbc.query(sql, ROW_MAPPER);
    }

    public Optional<Supplier> findById(Integer id) {
        String sql = "SELECT supplier_id, name, phone, email, address FROM supplier WHERE supplier_id = ?";
        try {
            Supplier s = jdbc.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(s);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Supplier save(Supplier supplier) {
        if (supplier.getSupplier_id() == null) {
            final String insertSql = "INSERT INTO supplier (name, phone, email, address) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, supplier.getName());
                ps.setString(2, supplier.getPhone());
                ps.setString(3, supplier.getEmail());
                ps.setString(4, supplier.getAddress());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                supplier.setSupplier_id(key.intValue());
            }
            return supplier;
        } else {
            final String updateSql = "UPDATE supplier SET name = ?, phone = ?, email = ?, address = ? WHERE supplier_id = ?";
            jdbc.update(updateSql,
                    supplier.getName(),
                    supplier.getPhone(),
                    supplier.getEmail(),
                    supplier.getAddress(),
                    supplier.getSupplier_id());
            return supplier;
        }
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM supplier WHERE supplier_id = ?";
        return jdbc.update(sql, id);
    }
}