package com.plant_management.dao;

import com.plant_management.model.Delivery;
import com.plant_management.model.Order;
import com.plant_management.model.Vehicle;
import com.plant_management.model.Driver;
import com.plant_management.model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
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

        if (rs.getTimestamp("departure_time") != null) {
            d.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        }
        if (rs.getTimestamp("delivery_time") != null) {
            d.setDeliveryTime(rs.getTimestamp("delivery_time").toLocalDateTime());
        }

        // ==========================
        // 1. Map Nested Order
        // ==========================
        int orderId = rs.getInt("order_id");
        if (!rs.wasNull()) {
            Order o = new Order();
            o.setOrder_id(orderId);

            // Map Customer ID
            int customerId = rs.getInt("customer_id");
            if (!rs.wasNull()) {
                o.setCustomer_id(customerId); // Change to o.setCustomer(new Customer(customerId)) if your model uses an object
            }

            // Map Employee ID (Mapped as order_employee_id in the view to avoid clash with driver's employee_id)
            int orderEmployeeId = rs.getInt("order_employee_id");
            if (!rs.wasNull()) {
                o.setEmployee_id(orderEmployeeId); // Change to o.setEmployee(new Employee(orderEmployeeId)) if needed
            }

            java.sql.Date sqlOrderDate = rs.getDate("order_date");
            if (sqlOrderDate != null) {
                o.setOrder_date(sqlOrderDate.toLocalDate());
            }

            o.setAddress(rs.getString("order_address"));

            // Map Order Status (Enum safe-mapping)
            String orderStatusStr = rs.getString("order_status");
            if (orderStatusStr != null) {
                String formattedStatus = orderStatusStr.replace(" ", "_");
                for (Order.OrderStatus statusEnum : Order.OrderStatus.values()) { // Adjust "OrderStatus" to match your actual Enum name
                    if (statusEnum.name().equalsIgnoreCase(formattedStatus)) {
                        o.setStatus(statusEnum);
                        break;
                    }
                }
            }

            d.setOrder(o);
        }

        // ==========================
        // 2. Map Nested Vehicle
        // ==========================
        int vehicleId = rs.getInt("vehicle_id");
        if (!rs.wasNull()) {
            Vehicle v = new Vehicle();
            v.setVehicle_id(vehicleId);

            // Map Vehicle Type (Enum safe-mapping)
            String vehicleTypeStr = rs.getString("vehicle_type");
            if (vehicleTypeStr != null) {
                String formattedType = vehicleTypeStr.replace(" ", "_");
                for (Vehicle.VehicleType typeEnum : Vehicle.VehicleType.values()) { // Adjust "VehicleType" to match your actual Enum name
                    if (typeEnum.name().equalsIgnoreCase(formattedType)) {
                        v.setType(typeEnum);
                        break;
                    }
                }
            }

            v.setLicense_plate(rs.getString("license_plate"));
            v.setModel(rs.getString("model"));
            v.setCapacity(rs.getFloat("capacity"));

            // Existing Vehicle Status Mapping...
            String vehicleStatusStr = rs.getString("vehicle_status");
            if (vehicleStatusStr != null) {
                String formattedDbString = vehicleStatusStr.replace(" ", "_");
                for (Vehicle.Status statusEnum : Vehicle.Status.values()) {
                    if (statusEnum.name().equalsIgnoreCase(formattedDbString)) {
                        v.setStatus(statusEnum);
                        break;
                    }
                }
            }

            d.setVehicle(v);
        }

        // ==========================
        // 3. Map Nested Driver
        // ==========================
        int driverId = rs.getInt("driver_id");
        if (!rs.wasNull()) {
            Driver dr = new Driver();
            dr.setDriver_id(driverId);
            dr.setLicenseNo(rs.getString("license_no"));

            int driverEmployeeId = rs.getInt("driver_employee_id");
            if (!rs.wasNull()) {
                Employee emp = new Employee();
                emp.setEmployee_id(driverEmployeeId);
                dr.setEmployee(emp);
            }
            d.setDriver(dr);
        }

        return d;
    };

    public List<Delivery> findAll() {
        String sql = "SELECT * FROM v_delivery_details";
        return jdbc.query(sql, DELIVERY_ROW_MAPPER);
    }

    public Optional<Delivery> findById(Integer id) {
        String sql = "SELECT * FROM v_delivery_details WHERE delivery_id = ?";
        try {
            Delivery d = jdbc.queryForObject(sql, DELIVERY_ROW_MAPPER, id);
            return Optional.ofNullable(d);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Delivery insert(Delivery delivery) {
        // Inserts still target the base table, not the view
        final String sql = "INSERT INTO deliveries (order_id, vehicle_id, driver_id, departure_time, delivery_time) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"delivery_id"});

            if (delivery.getOrder() != null && delivery.getOrder().getOrder_id() != null && delivery.getOrder().getOrder_id() > 0) {
                ps.setObject(1, delivery.getOrder().getOrder_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            if (delivery.getVehicle() != null && delivery.getVehicle().getVehicle_id() != null && delivery.getVehicle().getVehicle_id() > 0) {
                ps.setObject(2, delivery.getVehicle().getVehicle_id(), Types.INTEGER);
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (delivery.getDriver() != null && delivery.getDriver().getDriver_id() != null && delivery.getDriver().getDriver_id() > 0) {
                ps.setObject(3, delivery.getDriver().getDriver_id(), Types.INTEGER);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (delivery.getDepartureTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(delivery.getDepartureTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

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
            // Updates still target the base table
            String sql = "UPDATE deliveries SET order_id = ?, vehicle_id = ?, driver_id = ?, departure_time = ?, delivery_time = ? WHERE delivery_id = ?";

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
        String sql = "DELETE FROM deliveries WHERE delivery_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM deliveries WHERE delivery_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public List<Delivery> findPendingDeliveries() {
        String sql = "SELECT * FROM v_delivery_details WHERE delivery_time IS NULL";
        return jdbc.query(sql, DELIVERY_ROW_MAPPER);
    }

    public List<Delivery> findCompletedDeliveries() {
        String sql = "SELECT * FROM v_delivery_details WHERE delivery_time IS NOT NULL";
        return jdbc.query(sql, DELIVERY_ROW_MAPPER);
    }

    public boolean existsByOrder(Order order) {
        if (order == null || order.getOrder_id() == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM deliveries WHERE order_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, order.getOrder_id());
        return count != null && count > 0;
    }
}