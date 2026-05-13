// java
package com.plant_management.dao;

import com.plant_management.model.Driver;
import com.plant_management.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class DriverDao {

    private final JdbcTemplate jdbc;

    public DriverDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Map driver + optional employee columns -> Driver
    private final RowMapper<Driver> ROW_MAPPER = (rs, rowNum) -> {
        Driver d = new Driver();
        d.setDriver_id(rs.getObject("driver_id") != null ? rs.getInt("driver_id") : null);

        int empId = rs.getInt("employee_id");
        if (!rs.wasNull()) {
            Employee e = new Employee();
            e.setEmployee_id(empId);

            String first = rs.getString("first_name");
            if (first != null) e.setFirst_name(first);

            String last = rs.getString("last_name");
            if (last != null) e.setLast_name(last);

            String cnic = rs.getString("cnic");
            if (cnic != null) e.setCnic(cnic);

            d.setEmployee(e);
        } else {
            d.setEmployee(null);
        }

        d.setLicenseNo(rs.getString("license_no"));
        return d;
    };

    public List<Driver> findAll() {
        // Join to bring employee columns into the result set
        String sql = "SELECT * FROM v_all_drivers";
        return jdbc.query(sql, ROW_MAPPER);
    }

    public Optional<Driver> findById(Integer id) {
        // Use stored function that returns compatible column names
        String sql = "SELECT * FROM get_driver_info(?)";
        try {
            Driver d = jdbc.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(d);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Driver save(Driver driver) {
        if (driver.getDriver_id() == null) {
            final String insertSql = "INSERT INTO driver (employee_id, license_no) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"driver_id"});
                if (driver.getEmployee() != null && driver.getEmployee().getEmployee_id() != null) {
                    ps.setObject(1, driver.getEmployee().getEmployee_id(), Types.INTEGER);
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                ps.setString(2, driver.getLicenseNo());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                driver.setDriver_id(key.intValue());
            }
            return driver;
        } else {
            final String updateSql = "UPDATE driver SET employee_id = ?, license_no = ? WHERE driver_id = ?";
            Object empId = (driver.getEmployee() != null && driver.getEmployee().getEmployee_id() != null)
                    ? driver.getEmployee().getEmployee_id() : null;
            jdbc.update(updateSql,
                    empId,
                    driver.getLicenseNo(),
                    driver.getDriver_id());
            return driver;
        }
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM driver WHERE driver_id = ?";
        return jdbc.update(sql, id);
    }
}