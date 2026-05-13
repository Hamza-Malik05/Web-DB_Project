package com.plant_management.dao;

import com.plant_management.model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql. Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeDao {
    private final JdbcTemplate jdbcTemplate;

    public EmployeeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Employee> rowMapper = (rs, rowNum) -> {
        Employee e = new Employee();
        e.setEmployee_id(rs.getObject("employee_id") != null ? rs.getInt("employee_id") : null);
        e.setDept_id(rs.getObject("dept_id") != null ? rs.getInt("dept_id") : null);
        e.setFirst_name(rs.getString("first_name"));
        e.setLast_name(rs.getString("last_name"));
        Date dob = rs.getDate("date_of_birth");
        e.setDate_of_birth(dob);
        e.setCnic(rs.getString("cnic"));
        e.setEmail(rs.getString("email"));
        e.setDesignation(rs.getString("designation"));
        e.setAddress(rs.getString("address"));
        String genderStr = rs.getString("gender");
        if (genderStr != null) {
            try {
                e.setGender(Employee.Gender.valueOf(genderStr));
            } catch (IllegalArgumentException ex) {
                // ignore invalid value
                e.setGender(null);
            }
        }
        e.setAbsences(rs.getObject("absences") != null ? rs.getInt("absences") : null);
        e.setLeaves(rs.getObject("leaves") != null ? rs.getInt("leaves") : null);
        return e;
    };

    public List<Employee> findAll() {
        String sql = "SELECT employee_id, dept_id, first_name, last_name, date_of_birth, cnic, email, designation, address, gender, absences, leaves FROM employee ORDER BY employee_id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Employee> findById(Integer id) {
        String sql = "SELECT employee_id, dept_id, first_name, last_name, date_of_birth, cnic, email, designation, address, gender, absences, leaves FROM employee WHERE employee_id = ?";
        try {
            Employee e = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(e);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Employee insert(Employee employee) {
        String sql = "INSERT INTO employee (dept_id, first_name, last_name, date_of_birth, cnic, email, designation, address, gender, absences, leaves) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::gender_type, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"employee_id"});
            ps.setObject(1, employee.getDept_id());
            ps.setString(2, employee.getFirst_name());
            ps.setString(3, employee.getLast_name());
            ps.setDate(4, employee.getDate_of_birth());
            ps.setString(5, employee.getCnic());
            ps.setString(6, employee.getEmail());
            ps.setString(7, employee.getDesignation());
            ps.setString(8, employee.getAddress());
            ps.setString(9, employee.getGender() != null ? employee.getGender().name() : null);
            if (employee.getAbsences() != null) ps.setInt(10, employee.getAbsences()); else ps.setNull(10, java.sql.Types.INTEGER);
            if (employee.getLeaves() != null) ps.setInt(11, employee.getLeaves()); else ps.setNull(11, java.sql.Types.INTEGER);
            return ps;
        };

        jdbcTemplate.update(psc, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            employee.setEmployee_id(key.intValue());
        }
        return employee;
    }

    public int update(Employee employee) {
        String sql = "UPDATE employee SET dept_id = ?, first_name = ?, last_name = ?, date_of_birth = ?, cnic = ?, email = ?, designation = ?, address = ?, gender = ?::gender_type, absences = ?, leaves = ? WHERE employee_id = ?";
        return jdbcTemplate.update(sql,
                employee.getDept_id(),
                employee.getFirst_name(),
                employee.getLast_name(),
                employee.getDate_of_birth(),
                employee.getCnic(),
                employee.getEmail(),
                employee.getDesignation(),
                employee.getAddress(),
                employee.getGender() != null ? employee.getGender().name() : null,
                employee.getAbsences(),
                employee.getLeaves(),
                employee.getEmployee_id());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public List<Employee> findEmployeesNotRegisteredAsUsers() {
        String sql = "SELECT e.employee_id, e.dept_id, e.first_name, e.last_name, e.date_of_birth, e.cnic, e.email, e.designation, e.address, e.gender, e.absences, e.leaves " +
                "FROM employee e LEFT JOIN users u ON u.employee_id = e.employee_id WHERE u.employee_id IS NULL";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Employee> findEmployeesInProduction() {
        String sql = "SELECT employee_id, dept_id, first_name, last_name, date_of_birth, cnic, email, designation, address, gender, absences, leaves " +
                "FROM employee WHERE LOWER(designation) LIKE '%production%'";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Employee> findSalesReps() {
        String sql = "SELECT employee_id, dept_id, first_name, last_name, date_of_birth, cnic, email, designation, address, gender, absences, leaves " +
                "FROM employee WHERE LOWER(designation) LIKE '%sales%'";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM employee WHERE employee_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}