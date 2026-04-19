package com.plant_management.dao;

import com.plant_management.model.Delivery;
import com.plant_management.model.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class DeliveryDao {

    private final JdbcTemplate jdbc;

    public DeliveryDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Delivery> DELIVERY_ROW_MAPPER = (rs, rowNum) -> {
        Delivery d = new Delivery();
        d.setDelivery_id(rs.getInt("delivery_id"));

        // Entity mappings omitted here to avoid hard dependency; load via respective DAOs if needed
        d.setOrder(null);
        d.setVehicle(null);
        d.setDriver(null);

        if (rs.getTimestamp("departure_time") != null) {
            d.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        }
        if (rs.getTimestamp("delivery_time") != null) {
            d.setDeliveryTime(rs.getTimestamp("delivery_time").toLocalDateTime());
        }

        return d;
    };

    public List<Delivery> findAll() {
        String sql = "SELECT delivery_id, order_id, vehicle_id, driver_id, departure_time, delivery_time FROM delivery";
        return jdbc.query(sql, DELIVERY_ROW_MAPPER);
    }

    public Optional<Delivery> findById(Integer id) {
        String sql = "SELECT delivery_id, order_id, vehicle_id, driver_id, departure_time, delivery_time FROM delivery WHERE delivery_id = ?";
        try {
            Delivery d = jdbc.queryForObject(sql, DELIVERY_ROW_MAPPER, id);
            return Optional.ofNullable(d);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Delivery insert(Delivery delivery) {
        final String sql = "INSERT INTO delivery (order_id, vehicle_id, driver_id, departure_time, delivery_time) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // 1. Order ID
            if (delivery.getOrder() != null && delivery.getOrder().getOrder_id() != null && delivery.getOrder().getOrder_id() > 0) {
                ps.setObject(1, delivery.getOrder().getOrder_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            // 2. Vehicle ID
            if (delivery.getVehicle() != null && delivery.getVehicle().getVehicle_id() != null && delivery.getVehicle().getVehicle_id() > 0) {
                ps.setObject(2, delivery.getVehicle().getVehicle_id(), Types.INTEGER);
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            // 3. Driver ID
            if (delivery.getDriver() != null && delivery.getDriver().getDriver_id() != null && delivery.getDriver().getDriver_id() > 0) {
                ps.setObject(3, delivery.getDriver().getDriver_id(), Types.INTEGER);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            // 4. Departure Time
            if (delivery.getDepartureTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(delivery.getDepartureTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            // 5. Delivery Time
            if (delivery.getDeliveryTime() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(delivery.getDeliveryTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            delivery.setDelivery_id(key.intValue());
        }
        return delivery;
    }

    public Delivery save(Delivery delivery) {
        if (delivery.getDelivery_id() != null && delivery.getDelivery_id() > 0) {
            String sql = "UPDATE delivery SET order_id = ?, vehicle_id = ?, driver_id = ?, departure_time = ?, delivery_time = ? WHERE delivery_id = ?";

            Object orderId = (delivery.getOrder() != null && delivery.getOrder().getOrder_id() != null && delivery.getOrder().getOrder_id() > 0)
                    ? delivery.getOrder().getOrder_id() : null;
            Object vehicleId = (delivery.getVehicle() != null && delivery.getVehicle().getVehicle_id() != null && delivery.getVehicle().getVehicle_id() > 0)
                    ? delivery.getVehicle().getVehicle_id() : null;
            Object driverId = (delivery.getDriver() != null && delivery.getDriver().getDriver_id() != null && delivery.getDriver().getDriver_id() > 0)
                    ? delivery.getDriver().getDriver_id() : null;

            Timestamp departureTime = delivery.getDepartureTime() != null ? Timestamp.valueOf(delivery.getDepartureTime()) : null;
            Timestamp deliveryTime = delivery.getDeliveryTime() != null ? Timestamp.valueOf(delivery.getDeliveryTime()) : null;

            jdbc.update(sql, orderId, vehicleId, driverId, departureTime, deliveryTime, delivery.getDelivery_id());
            return delivery;
        } else {
            return insert(delivery);
        }
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM delivery WHERE delivery_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM delivery WHERE delivery_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ==========================================
    // CUSTOM METHODS (Translated from JPA file)
    // ==========================================

    public List<Delivery> findPendingDeliveries() {
        String sql = "SELECT delivery_id, order_id, vehicle_id, driver_id, departure_time, delivery_time FROM delivery WHERE delivery_time IS NULL";
        return jdbc.query(sql, DELIVERY_ROW_MAPPER);
    }

    public List<Delivery> findCompletedDeliveries() {
        String sql = "SELECT delivery_id, order_id, vehicle_id, driver_id, departure_time, delivery_time FROM delivery WHERE delivery_time IS NOT NULL";
        return jdbc.query(sql, DELIVERY_ROW_MAPPER);
    }

    public boolean existsByOrder(Order order) {
        if (order == null || order.getOrder_id() == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM delivery WHERE order_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, order.getOrder_id());
        return count != null && count > 0;
    }
}