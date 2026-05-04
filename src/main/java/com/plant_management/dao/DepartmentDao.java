// File: `src/main/java/com/plant_management/dao/DepartmentRepository.java`
package com.plant_management.dao;

import com.plant_management.model.Department;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentDao {
    private final JdbcTemplate jdbcTemplate;

    public DepartmentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Department> rowMapper = (rs, rowNum) -> {
        Department d = new Department();
        d.setDept_id(rs.getObject("dept_id") != null ? rs.getInt("dept_id") : null);
        d.setName(rs.getString("name"));
        return d;
    };

    public Optional<Department> findById(Integer id) {
        String sql = "SELECT dept_id, name FROM department WHERE dept_id = ?";
        try {
            Department d = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(d);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Department> findAll() {
        String sql = "SELECT dept_id, name FROM department ORDER BY dept_id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Department insert(Department department) {
        String sql = "INSERT INTO department (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator psc = connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"dept_id"});
            ps.setString(1, department.getName());
            return ps;
        };

        jdbcTemplate.update(psc, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            department.setDept_id(key.intValue());
        }
        return department;
    }

    public int update(Department department) {
        String sql = "UPDATE department SET name = ? WHERE dept_id = ?";
        return jdbcTemplate.update(sql, department.getName(), department.getDept_id());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM department WHERE dept_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(1) FROM department WHERE dept_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}