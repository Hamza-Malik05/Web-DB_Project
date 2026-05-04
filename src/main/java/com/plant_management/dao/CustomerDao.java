
package com.plant_management.dao;

import com.plant_management.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDao {

    private final JdbcTemplate jdbc;

    public CustomerDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Customer> ROW_MAPPER = (rs, rowNum) -> {
        Customer c = new Customer();
        c.setCustomer_id(rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null);
        c.setCustomer_name(rs.getString("customer_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        return c;
    };

    public List<Customer> findAll() {
        String sql = "SELECT customer_id, customer_name, phone, email, address FROM customers";
        return jdbc.query(sql, ROW_MAPPER);
    }

    public Optional<Customer> findById(Integer id) {
        String sql = "SELECT customer_id, customer_name, phone, email, address FROM customers WHERE customer_id = ?";
        try {
            Customer c = jdbc.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(c);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Customer save(Customer customer) {
        if (customer.getCustomer_id() == null) {
            final String insertSql = "INSERT INTO customers (customer_name, phone, email, address) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                // Explicitly name the primary key column
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"customer_id"});
                ps.setString(1, customer.getCustomer_name());
                ps.setString(2, customer.getPhone());
                ps.setString(3, customer.getEmail());
                ps.setString(4, customer.getAddress());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                customer.setCustomer_id(key.intValue());
            }
            return customer;
        } else {
            final String updateSql = "UPDATE customers SET customer_name = ?, phone = ?, email = ?, address = ? WHERE customer_id = ?";
            jdbc.update(updateSql,
                    customer.getCustomer_name(),
                    customer.getPhone(),
                    customer.getEmail(),
                    customer.getAddress(),
                    customer.getCustomer_id());
            return customer;
        }
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        return jdbc.update(sql, id);
    }
}