package com.plant_management.dao;

import com.plant_management.model.Accountant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountantDao {

    private final JdbcTemplate jdbc;

    public AccountantDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Accountant> ACCOUNTANT_ROW_MAPPER = (rs, rowNum) -> {
        Accountant a = new Accountant();
        a.setAccountant_id(rs.getInt("accountant_id"));
        // employee mapping omitted here to avoid hard dependency; load via EmployeeDao if needed
        a.setEmployee(null);
        a.setDomain(rs.getString("domain"));
        return a;
    };

    public List<Accountant> findAll() {
        String sql = "SELECT accountant_id, employee_id, domain FROM accountants";
        return jdbc.query(sql, ACCOUNTANT_ROW_MAPPER);
    }

    public Optional<Accountant> findById(Integer id) {
        String sql = "SELECT accountant_id, employee_id, domain FROM accountants WHERE accountant_id = ?";
        try {
            Accountant a = jdbc.queryForObject(sql, ACCOUNTANT_ROW_MAPPER, id);
            return Optional.ofNullable(a);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Accountant insert(Accountant accountant) {
        final String sql = "INSERT INTO accountants (employee_id, domain) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (accountant.getEmployee() != null && accountant.getEmployee().getEmployee_id() != 0) {
                ps.setObject(1, accountant.getEmployee().getEmployee_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, accountant.getDomain());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            accountant.setAccountant_id(key.intValue());
        }
        return accountant;
    }

    public Accountant save(Accountant accountant) {
        if (accountant.getAccountant_id() > 0) {
            String sql = "UPDATE accountants SET employee_id = ?, domain = ? WHERE accountant_id = ?";
            Object empId = (accountant.getEmployee() != null && accountant.getEmployee().getEmployee_id() != 0)
                    ? accountant.getEmployee().getEmployee_id() : null;
            jdbc.update(sql, empId, accountant.getDomain(), accountant.getAccountant_id());
            return accountant;
        } else {
            return insert(accountant);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM accountants WHERE accountant_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM accountants WHERE accountant_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}