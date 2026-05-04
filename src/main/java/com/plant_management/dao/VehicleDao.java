package com.plant_management.dao;

import com.plant_management.model.Vehicle;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleDao {

    private final JdbcTemplate jdbc;

    public VehicleDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Vehicle> VEHICLE_ROW_MAPPER = (rs, rowNum) -> {
        Vehicle v = new Vehicle();
        v.setVehicle_id(rs.getInt("vehicle_id"));
        v.setLicense_plate(rs.getString("license_plate"));
        v.setModel(rs.getString("model"));

        // Use getObject to safely handle potential database NULLs mapped to Java Float objects
        v.setCapacity(rs.getObject("capacity", Float.class));

        // 1. Safely Map Vehicle Type Enum
        String typeStr = rs.getString("type");
        if (typeStr != null) {
            for (Vehicle.VehicleType typeEnum : Vehicle.VehicleType.values()) {
                if (typeEnum.name().equalsIgnoreCase(typeStr.replace(" ", "_"))) {
                    v.setType(typeEnum);
                    break;
                }
            }
        }

        // 2. Safely Map Vehicle Status Enum
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            for (Vehicle.Status statusEnum : Vehicle.Status.values()) {
                if (statusEnum.name().equalsIgnoreCase(statusStr.replace(" ", "_"))) {
                    v.setStatus(statusEnum);
                    break;
                }
            }
        }

        return v;
    };

    public List<Vehicle> findAll() {
        String sql = "SELECT vehicle_id, type, license_plate, model, capacity, status FROM vehicles";
        return jdbc.query(sql, VEHICLE_ROW_MAPPER);
    }

    public Optional<Vehicle> findById(Integer id) {
        String sql = "SELECT vehicle_id, type, license_plate, model, capacity, status FROM vehicles WHERE vehicle_id = ?";
        try {
            Vehicle v = jdbc.queryForObject(sql, VEHICLE_ROW_MAPPER, id);
            return Optional.ofNullable(v);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Vehicle insert(Vehicle vehicle) {
        // Explicitly cast the enums with ?::vehicle_type and ?::vehicle_status
        final String sql = "INSERT INTO vehicles (type, license_plate, model, capacity, status) VALUES (?::vehicle_type, ?, ?, ?, ?::vehicle_status)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            // PostgreSQL fix for returning keys
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"vehicle_id"});

            if (vehicle.getType() != null) {
                ps.setString(1, vehicle.getType().name());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }

            ps.setString(2, vehicle.getLicense_plate());
            ps.setString(3, vehicle.getModel());

            if (vehicle.getCapacity() != null) {
                ps.setFloat(4, vehicle.getCapacity());
            } else {
                ps.setNull(4, Types.FLOAT);
            }

            if (vehicle.getStatus() != null) {
                ps.setString(5, vehicle.getStatus().name());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            vehicle.setVehicle_id(key.intValue());
        }
        return vehicle;
    }

    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getVehicle_id() != null && vehicle.getVehicle_id() > 0) {
            // Explicitly cast the enums for the update query
            String sql = "UPDATE vehicles SET type = ?::vehicle_type, license_plate = ?, model = ?, capacity = ?, status = ?::vehicle_status WHERE vehicle_id = ?";

            String typeStr = vehicle.getType() != null ? vehicle.getType().name() : null;
            String statusStr = vehicle.getStatus() != null ? vehicle.getStatus().name() : null;

            jdbc.update(sql, typeStr, vehicle.getLicense_plate(), vehicle.getModel(), vehicle.getCapacity(), statusStr, vehicle.getVehicle_id());
            return vehicle;
        } else {
            return insert(vehicle);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE vehicle_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ==========================================
    // CUSTOM METHODS (Translated from JPA repository)
    // ==========================================

    public List<Vehicle> findByStatus(Vehicle.Status status) {
        if (status == null) {
            return List.of();
        }
        // Explicitly cast the parameter here as well
        String sql = "SELECT vehicle_id, type, license_plate, model, capacity, status FROM vehicles WHERE status = ?::vehicle_status";
        return jdbc.query(sql, VEHICLE_ROW_MAPPER, status.name());
    }
}