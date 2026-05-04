package com.plant_management.dao;

import com.plant_management.model.Employee;
import com.plant_management.model.Supervisor;
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
public class SupervisorDao {

    private final JdbcTemplate jdbc;

    public SupervisorDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Supervisor> SUPERVISOR_ROW_MAPPER = (rs, rowNum) -> {
        Supervisor s = new Supervisor();
        s.setSupervisor_id(rs.getInt("supervisor_id"));
        s.setOfficeNo(rs.getString("office_no"));

        // Creating a dummy Employee object to hold the foreign key ID
        int employeeId = rs.getInt("employee_id");
        if (!rs.wasNull()) {
            Employee employee = new Employee();
            employee.setEmployee_id(employeeId);
            s.setEmployee(employee);
        } else {
            s.setEmployee(null);
        }

        return s;
    };

    public List<Supervisor> findAll() {
        String sql = "SELECT supervisor_id, employee_id, office_no FROM supervisor";
        return jdbc.query(sql, SUPERVISOR_ROW_MAPPER);
    }

    public Optional<Supervisor> findById(Integer id) {
        String sql = "SELECT supervisor_id, employee_id, office_no FROM supervisor WHERE supervisor_id = ?";
        try {
            Supervisor s = jdbc.queryForObject(sql, SUPERVISOR_ROW_MAPPER, id);
            return Optional.ofNullable(s);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Supervisor insert(Supervisor supervisor) {
        final String sql = "INSERT INTO supervisor (employee_id, office_no) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"supervisor_id"});

            // Handle Employee Foreign Key
            if (supervisor.getEmployee() != null && supervisor.getEmployee().getEmployee_id() != null && supervisor.getEmployee().getEmployee_id() > 0) {
                ps.setObject(1, supervisor.getEmployee().getEmployee_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            // Handle Office Number
            ps.setString(2, supervisor.getOfficeNo());

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            supervisor.setSupervisor_id(key.intValue());
        }
        return supervisor;
    }

    public Supervisor save(Supervisor supervisor) {
        if (supervisor.getSupervisor_id() > 0) {
            String sql = "UPDATE supervisor SET employee_id = ?, office_no = ? WHERE supervisor_id = ?";

            Object empId = (supervisor.getEmployee() != null && supervisor.getEmployee().getEmployee_id() != null && supervisor.getEmployee().getEmployee_id() > 0)
                    ? supervisor.getEmployee().getEmployee_id() : null;

            jdbc.update(sql, empId, supervisor.getOfficeNo(), supervisor.getSupervisor_id());
            return supervisor;
        } else {
            return insert(supervisor);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM supervisor WHERE supervisor_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM supervisor WHERE supervisor_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}