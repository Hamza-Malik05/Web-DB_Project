package com.plant_management.dao;

import com.plant_management.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
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

        // Safely map Database String to Java Enum
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            for (Order.OrderStatus statusEnum : Order.OrderStatus.values()) {
                if (statusEnum.name().equalsIgnoreCase(statusStr.replace(" ", "_"))) {
                    // Note: Change to setOrderStatus() if your Lombok setter is named that way
                    o.setStatus(statusEnum);
                    break;
                }
            }
        }

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

    // Updated parameter to use Enum to ensure type safety
    public List<Order> findByStatus(Order.OrderStatus status) {
        // Explicitly cast the parameter to your database enum type
        String sql = "SELECT order_id, customer_id, employee_id, order_date, status, address FROM orders WHERE status = ?::order_status ORDER BY order_id";
        return jdbc.query(sql, ROW_MAPPER, status.name());
    }

    public Order save(Order order) {
        if (order.getOrder_id() == null) {
            // Note the explicit cast ?::order_status for the status column
            final String insertSql = "INSERT INTO orders (customer_id, employee_id, order_date, status, address) VALUES (?, ?, ?, ?::order_status, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                // PostgreSQL specific fix for returning generated keys
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"order_id"});

                if (order.getCustomer_id() != null) ps.setObject(1, order.getCustomer_id(), Types.INTEGER); else ps.setNull(1, Types.INTEGER);
                if (order.getEmployee_id() != null) ps.setObject(2, order.getEmployee_id(), Types.INTEGER); else ps.setNull(2, Types.INTEGER);
                if (order.getOrder_date() != null) ps.setDate(3, Date.valueOf(order.getOrder_date())); else ps.setNull(3, Types.DATE);

                // Get the string value from the enum
                if (order.getStatus() != null) ps.setString(4, order.getStatus().name()); else ps.setNull(4, Types.VARCHAR);

                if (order.getAddress() != null) ps.setString(5, order.getAddress()); else ps.setNull(5, Types.VARCHAR);
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) order.setOrder_id(key.intValue());
            return order;
        } else {
            // Note the explicit cast ?::order_status here as well
            final String updateSql = "UPDATE orders SET customer_id = ?, employee_id = ?, order_date = ?, status = ?::order_status, address = ? WHERE order_id = ?";
            jdbc.update(updateSql,
                    order.getCustomer_id(),
                    order.getEmployee_id(),
                    order.getOrder_date() != null ? Date.valueOf(order.getOrder_date()) : null,
                    order.getStatus() != null ? order.getStatus().name() : null,
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