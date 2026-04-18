// java
package com.plant_management.dao;

import com.plant_management.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

        private final JdbcTemplate jdbc;

        public UserDao(JdbcTemplate jdbc) {
                this.jdbc = jdbc;
        }

        private final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
                User u = new User();
                u.setUser_id(rs.getObject("user_id") != null ? rs.getInt("user_id") : null);
                // employee mapping omitted here to avoid a hard dependency; set null or load via EmployeeDao if needed
                u.setEmployee(null);
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                String role = rs.getString("role");
                if (role != null) {
                        try {
                                u.setRole(User.Role.valueOf(role));
                        } catch (IllegalArgumentException e) {
                                u.setRole(null);
                        }
                } else {
                        u.setRole(null);
                }
                return u;
        };

        public Optional<User> findByUsername(String username) {
                String sql = "SELECT user_id, employee_id, username, password, role FROM users WHERE username = ?";
                try {
                        User user = jdbc.queryForObject(sql, USER_ROW_MAPPER, username);
                        return Optional.ofNullable(user);
                } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
                        return Optional.empty();
                }
        }

        public boolean existsByUsername(String username) {
                String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
                Integer count = jdbc.queryForObject(sql, Integer.class, username);
                return count != null && count > 0;
        }

        /**
         * Calls the stored procedure / function to register a user from an employee.
         * Adjust the call syntax if your DB expects a different form (e.g. "SELECT register_user_from_employee(?, ?, ?)" for functions).
         */
        public void registerUserFromEmployee(Integer empId, String username, String password) {
                String call = "CALL register_user_from_employee(?, ?, ?)";
                jdbc.update(call, empId, username, password);
        }

        // --- CRUD methods added below ---

        public User insert(User user) {
                final String sql = "INSERT INTO users (employee_id, username, password, role) VALUES (?, ?, ?, ?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbc.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        if (user.getEmployee() != null && user.getEmployee().getEmployee_id() != null) {
                                ps.setObject(1, user.getEmployee().getEmployee_id(), Types.INTEGER);
                        } else {
                                ps.setNull(1, Types.INTEGER);
                        }
                        ps.setString(2, user.getUsername());
                        ps.setString(3, user.getPassword());
                        ps.setString(4, user.getRole() != null ? user.getRole().name() : null);
                        return ps;
                }, keyHolder);

                Number key = keyHolder.getKey();
                if (key != null) {
                        user.setUser_id(key.intValue());
                }
                return user;
        }

        public Optional<User> findById(Long userId) {
                String sql = "SELECT user_id, employee_id, username, password, role FROM users WHERE user_id = ?";
                try {
                        User u = jdbc.queryForObject(sql, USER_ROW_MAPPER, userId);
                        return Optional.ofNullable(u);
                } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
                        return Optional.empty();
                }
        }

        public List<User> findAll() {
                String sql = "SELECT user_id, employee_id, username, password, role FROM users";
                return jdbc.query(sql, USER_ROW_MAPPER);
        }

        public User save(User user) {
                if (user.getUser_id() == null) {
                        return insert(user);
                }
                String sql = "UPDATE users SET employee_id = ?, username = ?, password = ?, role = ? WHERE user_id = ?";
                Object empId = (user.getEmployee() != null && user.getEmployee().getEmployee_id() != null)
                        ? user.getEmployee().getEmployee_id() : null;
                jdbc.update(sql, empId, user.getUsername(), user.getPassword(),
                        user.getRole() != null ? user.getRole().name() : null, user.getUser_id());
                return user;
        }

        public void deleteById(Long userId) {
                String sql = "DELETE FROM users WHERE user_id = ?";
                jdbc.update(sql, userId);
        }

        public boolean existsById(Long userId) {
                String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
                Integer count = jdbc.queryForObject(sql, Integer.class, userId);
                return count != null && count > 0;
        }
}