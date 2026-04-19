package com.plant_management.dao;

import com.plant_management.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDao {

    private final JdbcTemplate jdbc;

    public OrderDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Order> ROW_MAPPER = (rs, rowNum) -> {
        Order o = new Order();
        o.setOrder_id(rs.getObject("order_id") != null ? rs.getInt("order_id") : null);
        o.setCustomer_id(rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null);
        o.setEmployee_id(rs.getObject("employee_id") != null ? rs.getInt("employee_id") : null);
        Date d = rs.getDate("order_date");
        if (d != null) o.setOrder_date(d.toLocalDate());
        o.setStatus(rs.getString("status"));
        o.setAddress(rs.getString("address"));
        return o;
    };

    public List<Order> findAll() {
        String sql = "SELECT order_id, customer_id, employee_id, order_date, status, address FROM orders ORDER BY order_id";
        return jdbc.query(sql, ROW_MAPPER);
    }

    public Optional<Order> findById(Integer id) {
        String sql = "SELECT order_id, customer_id, employee_id, order_date, status, address FROM orders WHERE order_id = ?";
        try {
            Order o = jdbc.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(o);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Order> findByStatus(String status) {
        String sql = "SELECT order_id, customer_id, employee_id, order_date, status, address FROM orders WHERE status = ? ORDER BY order_id";
        return jdbc.query(sql, ROW_MAPPER, status);
    }

    public Order save(Order order) {
        if (order.getOrder_id() == null) {
            final String insertSql = "INSERT INTO orders (customer_id, employee_id, order_date, status, address) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                if (order.getCustomer_id() != null) ps.setObject(1, order.getCustomer_id(), Types.INTEGER); else ps.setNull(1, Types.INTEGER);
                if (order.getEmployee_id() != null) ps.setObject(2, order.getEmployee_id(), Types.INTEGER); else ps.setNull(2, Types.INTEGER);
                if (order.getOrder_date() != null) ps.setDate(3, Date.valueOf(order.getOrder_date())); else ps.setNull(3, Types.DATE);
                if (order.getStatus() != null) ps.setString(4, order.getStatus()); else ps.setNull(4, Types.VARCHAR);
                if (order.getAddress() != null) ps.setString(5, order.getAddress()); else ps.setNull(5, Types.VARCHAR);
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) order.setOrder_id(key.intValue());
            return order;
        } else {
            final String updateSql = "UPDATE orders SET customer_id = ?, employee_id = ?, order_date = ?, status = ?, address = ? WHERE order_id = ?";
            jdbc.update(updateSql,
                    order.getCustomer_id(),
                    order.getEmployee_id(),
                    order.getOrder_date() != null ? Date.valueOf(order.getOrder_date()) : null,
                    order.getStatus(),
                    order.getAddress(),
                    order.getOrder_id());
            return order;
        }
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        return jdbc.update(sql, id);
    }

    public Optional<Integer> findMaxOrderId() {
        String sql = "SELECT MAX(order_id) FROM orders";
        Integer max = jdbc.queryForObject(sql, Integer.class);
        return Optional.ofNullable(max);
    }
}