package com.plant_management.dao;

import com.plant_management.model.Employee;
import com.plant_management.model.Salaries;
import com.plant_management.model.Transaction;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class SalariesDao {

    private final JdbcTemplate jdbc;

    public SalariesDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Salaries> SALARIES_ROW_MAPPER = (rs, rowNum) -> {
        Salaries s = new Salaries();
        s.setSalaryId(rs.getInt("salary_id"));

        // Set up Transaction dummy object for foreign key
        int transactionId = rs.getInt("transaction_id");
        if (!rs.wasNull()) {
            Transaction t = new Transaction();
            // Assuming your Transaction class has a setTransaction_id method
            t.setTransaction_id(transactionId);
            s.setTransaction(t);
        } else {
            s.setTransaction(null);
        }

        // Set up Employee dummy object for foreign key
        int employeeId = rs.getInt("employee_id");
        if (!rs.wasNull()) {
            Employee e = new Employee();
            // Assuming your Employee class has a setEmployee_id method
            e.setEmployee_id(employeeId);
            s.setEmployee(e);
        } else {
            s.setEmployee(null);
        }

        s.setBonus(rs.getBigDecimal("bonus"));
        s.setFine(rs.getBigDecimal("fine"));

        return s;
    };

    public List<Salaries> findAll() {
        String sql = "SELECT salary_id, transaction_id, employee_id, bonus, fine FROM salaries";
        return jdbc.query(sql, SALARIES_ROW_MAPPER);
    }

    public Optional<Salaries> findById(Integer id) {
        String sql = "SELECT salary_id, transaction_id, employee_id, bonus, fine FROM salaries WHERE salary_id = ?";
        try {
            Salaries s = jdbc.queryForObject(sql, SALARIES_ROW_MAPPER, id);
            return Optional.ofNullable(s);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Salaries insert(Salaries salary) {
        final String sql = "INSERT INTO salaries (transaction_id, employee_id, bonus, fine) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Transaction ID
            if (salary.getTransaction() != null && salary.getTransaction().getTransaction_id() != null && salary.getTransaction().getTransaction_id() > 0) {
                ps.setObject(1, salary.getTransaction().getTransaction_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            // Employee ID
            if (salary.getEmployee() != null && salary.getEmployee().getEmployee_id() != null && salary.getEmployee().getEmployee_id() > 0) {
                ps.setObject(2, salary.getEmployee().getEmployee_id(), Types.INTEGER);
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setBigDecimal(3, salary.getBonus());
            ps.setBigDecimal(4, salary.getFine());

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            salary.setSalaryId(key.intValue());
        }
        return salary;
    }

    public Salaries save(Salaries salary) {
        if (salary.getSalaryId() != null && salary.getSalaryId() > 0) {
            String sql = "UPDATE salaries SET transaction_id = ?, employee_id = ?, bonus = ?, fine = ? WHERE salary_id = ?";

            Object transactionId = (salary.getTransaction() != null && salary.getTransaction().getTransaction_id() != null && salary.getTransaction().getTransaction_id() > 0)
                    ? salary.getTransaction().getTransaction_id() : null;

            Object employeeId = (salary.getEmployee() != null && salary.getEmployee().getEmployee_id() != null && salary.getEmployee().getEmployee_id() > 0)
                    ? salary.getEmployee().getEmployee_id() : null;

            jdbc.update(sql, transactionId, employeeId, salary.getBonus(), salary.getFine(), salary.getSalaryId());
            return salary;
        } else {
            return insert(salary);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM salaries WHERE salary_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM salaries WHERE salary_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ==========================================
    // CUSTOM METHODS (Calling PostgreSQL Functions)
    // ==========================================

    public Optional<Salaries> findByEmployeeIdAndDate(Integer employeeId, Date date) {
        // Calls the database function get_salary_by_employee_and_date
        String sql = "SELECT * FROM get_salary_by_employee_and_date(?, ?)";
        try {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            Salaries s = jdbc.queryForObject(sql, SALARIES_ROW_MAPPER, employeeId, sqlDate);
            return Optional.ofNullable(s);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Salaries> findAllByDate(Date date) {
        // Calls the database function get_salaries_by_date
        String sql = "SELECT * FROM get_salaries_by_date(?)";
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return jdbc.query(sql, SALARIES_ROW_MAPPER, sqlDate);
    }

    public List<Salaries> findAllByEmployeeId(Integer employeeId) {
        // Simple select, no JOIN required
        String sql = "SELECT salary_id, transaction_id, employee_id, bonus, fine FROM salaries WHERE employee_id = ?";
        return jdbc.query(sql, SALARIES_ROW_MAPPER, employeeId);
    }
}