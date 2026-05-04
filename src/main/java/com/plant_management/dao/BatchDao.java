package com.plant_management.dao;

import com.plant_management.model.Batches;
import com.plant_management.model.Employee;
import com.plant_management.model.Products;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class BatchDao {

    private final JdbcTemplate jdbc;

    public BatchDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Batches> ROW_MAPPER = (rs, rowNum) -> {
        Batches b = new Batches();
        b.setBatch_id(rs.getObject("batch_id") != null ? rs.getInt("batch_id") : null);

        // Map Product Info from the JOIN
        int productId = rs.getInt("product_id");
        if (!rs.wasNull()) {
            Products p = new Products();
            p.setProduct_id(productId);
            p.setName(rs.getString("product_name")); // From JOIN
            p.setUnit_of_measurement(rs.getString("unit_of_measurement")); // From JOIN
            p.setPrice_per_unit(rs.getBigDecimal("price_per_unit")); // From JOIN
            b.setProduct(p);
        }

        // Map Employee (as you had it before)
        int empId = rs.getInt("employee_id");
        if (!rs.wasNull()) {
            Employee e = new Employee();
            e.setEmployee_id(empId);
            b.setEmployee(e);
        }

        b.setQuantity_used(rs.getObject("quantity_used") != null ? rs.getFloat("quantity_used") : null);
        b.setQuantity_produced(rs.getObject("quantity_produced") != null ? rs.getFloat("quantity_produced") : null);
        b.setStart_time(rs.getString("start_time"));
        b.setEnd_time(rs.getString("end_time"));

        return b;
    };

    public List<Batches> findAll() {
        String sql = "SELECT b.*, " +
                "p.name AS product_name, p.unit_of_measurement, p.price_per_unit " +
                "FROM batches b " +
                "LEFT JOIN products p ON b.product_id = p.product_id";
        return jdbc.query(sql, ROW_MAPPER);
    }

    public Optional<Batches> findById(Integer id) {
        String sql = "SELECT b.*, " +
                "p.name AS product_name, p.unit_of_measurement, p.price_per_unit " +
                "FROM batches b " +
                "LEFT JOIN products p ON b.product_id = p.product_id " +
                "WHERE b.batch_id = ?";
        try {
            Batches b = jdbc.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(b);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Batches save(Batches batch) {
        if (batch.getBatch_id() == null) {
            // FIX 1: Added ::timestamp to parameters 5 and 6
            final String insertSql = "INSERT INTO batches (product_id, employee_id, quantity_used, quantity_produced, start_time, end_time, r_storage_unit_id, p_storage_unit_id) VALUES (?, ?, ?, ?, ?::timestamp, ?::timestamp, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                // FIX 2: Explicitly ask for batch_id to prevent KeyHolder crashes
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"batch_id"});

                if (batch.getProduct() != null && batch.getProduct().getProduct_id() != null) {
                    ps.setObject(1, batch.getProduct().getProduct_id(), Types.INTEGER);
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                if (batch.getEmployee() != null && batch.getEmployee().getEmployee_id() != null) {
                    ps.setObject(2, batch.getEmployee().getEmployee_id(), Types.INTEGER);
                } else {
                    ps.setNull(2, Types.INTEGER);
                }
                if (batch.getQuantity_used() != null) {
                    ps.setFloat(3, batch.getQuantity_used());
                } else {
                    ps.setNull(3, Types.REAL);
                }
                if (batch.getQuantity_produced() != null) {
                    ps.setFloat(4, batch.getQuantity_produced());
                } else {
                    ps.setNull(4, Types.REAL);
                }
                ps.setString(5, batch.getStart_time());
                ps.setString(6, batch.getEnd_time());
                ps.setNull(7, Types.INTEGER); // r_storage_unit_id mapping omitted
                ps.setNull(8, Types.INTEGER); // p_storage_unit_id mapping omitted
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                batch.setBatch_id(key.intValue());
            }
            return batch;
        } else {
            // FIX 1 (Update block): Added ::timestamp to start_time and end_time assignments
            final String updateSql = "UPDATE batches SET product_id = ?, employee_id = ?, quantity_used = ?, quantity_produced = ?, start_time = ?::timestamp, end_time = ?::timestamp, r_storage_unit_id = ?, p_storage_unit_id = ? WHERE batch_id = ?";
            Object productId = (batch.getProduct() != null && batch.getProduct().getProduct_id() != null) ? batch.getProduct().getProduct_id() : null;
            Object employeeId = (batch.getEmployee() != null && batch.getEmployee().getEmployee_id() != null) ? batch.getEmployee().getEmployee_id() : null;

            jdbc.update(updateSql,
                    productId,
                    employeeId,
                    batch.getQuantity_used(),
                    batch.getQuantity_produced(),
                    batch.getStart_time(),
                    batch.getEnd_time(),
                    null, // r_storage_unit_id placeholder
                    null, // p_storage_unit_id placeholder
                    batch.getBatch_id());
            return batch;
        }
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM batches WHERE batch_id = ?";
        return jdbc.update(sql, id);
    }
}