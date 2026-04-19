package com.plant_management.dao;

import com.plant_management.model.Vehicle;
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
public class VehicleDao {

    private final JdbcTemplate jdbc;

    public VehicleDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Vehicle> VEHICLE_ROW_MAPPER = (rs, rowNum) -> {
        Vehicle v = new Vehicle();
        v.setVehicle_id(rs.getInt("vehicle_id"));
        v.setType(rs.getString("type"));
        v.setLicense_plate(rs.getString("license_plate"));
        v.setModel(rs.getString("model"));

        // Use getObject to safely handle potential database NULLs mapped to Java Float objects
        v.setCapacity(rs.getObject("capacity", Float.class));

        // Safely parse the enum from the database string
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                v.setStatus(Vehicle.Status.valueOf(statusStr.toLowerCase()));
            } catch (IllegalArgumentException e) {
                // If there's an unknown status in the DB, handle or leave null
                v.setStatus(null);
            }
        }

        return v;
    };

    public List<Vehicle> findAll() {
        String sql = "SELECT vehicle_id, type, license_plate, model, capacity, status FROM vehicle";
        return jdbc.query(sql, VEHICLE_ROW_MAPPER);
    }

    public Optional<Vehicle> findById(Integer id) {
        String sql = "SELECT vehicle_id, type, license_plate, model, capacity, status FROM vehicle WHERE vehicle_id = ?";
        try {
            Vehicle v = jdbc.queryForObject(sql, VEHICLE_ROW_MAPPER, id);
            return Optional.ofNullable(v);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Vehicle insert(Vehicle vehicle) {
        final String sql = "INSERT INTO vehicle (type, license_plate, model, capacity, status) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, vehicle.getType());
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
            String sql = "UPDATE vehicle SET type = ?, license_plate = ?, model = ?, capacity = ?, status = ? WHERE vehicle_id = ?";

            String statusStr = vehicle.getStatus() != null ? vehicle.getStatus().name() : null;

            jdbc.update(sql, vehicle.getType(), vehicle.getLicense_plate(), vehicle.getModel(), vehicle.getCapacity(), statusStr, vehicle.getVehicle_id());
            return vehicle;
        } else {
            return insert(vehicle);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM vehicle WHERE vehicle_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM vehicle WHERE vehicle_id = ?";
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
        String sql = "SELECT vehicle_id, type, license_plate, model, capacity, status FROM vehicle WHERE status = ?";
        return jdbc.query(sql, VEHICLE_ROW_MAPPER, status.name());
    }
}