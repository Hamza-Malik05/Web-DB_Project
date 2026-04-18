package com.plant_management.dao;

import com.plant_management.model.Attendance;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AttendanceDao {

    private final JdbcTemplate jdbc;

    public AttendanceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Attendance> ATTENDANCE_ROW_MAPPER = (rs, rowNum) -> {
        Attendance a = new Attendance();
        a.setAttendance_id(rs.getObject("attendance_id") != null ? rs.getInt("attendance_id") : null);
        // avoid a hard dependency on Employee DAO here; set to null or load via EmployeeDao if required
        a.setEmployee(null);
        Date d = rs.getDate("date");
        a.setDate(d != null ? d.toLocalDate() : null);
        Time tIn = rs.getTime("clock_in");
        a.setClock_in(tIn != null ? tIn.toLocalTime() : null);
        Time tOut = rs.getTime("clock_out");
        a.setClock_out(tOut != null ? tOut.toLocalTime() : null);
        String status = rs.getString("status");
        if (status != null) {
            try {
                a.setStatus(Attendance.Status.valueOf(status));
            } catch (IllegalArgumentException ex) {
                a.setStatus(null);
            }
        } else {
            a.setStatus(null);
        }
        return a;
    };

    public Optional<Attendance> findById(Integer id) {
        String sql = "SELECT attendance_id, employee_id, date, clock_in, clock_out, status FROM attendance WHERE attendance_id = ?";
        try {
            Attendance a = jdbc.queryForObject(sql, ATTENDANCE_ROW_MAPPER, id);
            return Optional.ofNullable(a);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Attendance> findByEmployeeIdAndDate(Integer employeeId, LocalDate date) {
        String sql = "SELECT attendance_id, employee_id, date, clock_in, clock_out, status FROM attendance WHERE employee_id = ? AND date = ?";
        try {
            Attendance a = jdbc.queryForObject(sql, ATTENDANCE_ROW_MAPPER, employeeId, Date.valueOf(date));
            return Optional.ofNullable(a);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Attendance> findByEmployeeId(Integer employeeId) {
        String sql = "SELECT attendance_id, employee_id, date, clock_in, clock_out, status FROM attendance WHERE employee_id = ?";
        return jdbc.query(sql, ATTENDANCE_ROW_MAPPER, employeeId);
    }

    public List<Attendance> findAllByDate(LocalDate date) {
        String sql = "SELECT attendance_id, employee_id, date, clock_in, clock_out, status FROM attendance WHERE date = ?";
        return jdbc.query(sql, ATTENDANCE_ROW_MAPPER, Date.valueOf(date));
    }

    public List<Attendance> findAll() {
        String sql = "SELECT attendance_id, employee_id, date, clock_in, clock_out, status FROM attendance";
        return jdbc.query(sql, ATTENDANCE_ROW_MAPPER);
    }

    public Attendance insert(Attendance attendance) {
        final String sql = "INSERT INTO attendance (employee_id, date, clock_in, clock_out, status) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (attendance.getEmployee() != null && attendance.getEmployee().getEmployee_id() != null) {
                ps.setObject(1, attendance.getEmployee().getEmployee_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setDate(2, attendance.getDate() != null ? Date.valueOf(attendance.getDate()) : null);
            if (attendance.getClock_in() != null) {
                ps.setTime(3, Time.valueOf(attendance.getClock_in()));
            } else {
                ps.setNull(3, Types.TIME);
            }
            if (attendance.getClock_out() != null) {
                ps.setTime(4, Time.valueOf(attendance.getClock_out()));
            } else {
                ps.setNull(4, Types.TIME);
            }
            ps.setString(5, attendance.getStatus() != null ? attendance.getStatus().name() : null);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            attendance.setAttendance_id(key.intValue());
        }
        return attendance;
    }

    public Attendance save(Attendance attendance) {
        if (attendance.getAttendance_id() == null) {
            return insert(attendance);
        }
        String sql = "UPDATE attendance SET employee_id = ?, date = ?, clock_in = ?, clock_out = ?, status = ? WHERE attendance_id = ?";
        Object empId = (attendance.getEmployee() != null && attendance.getEmployee().getEmployee_id() != null)
                ? attendance.getEmployee().getEmployee_id() : null;
        jdbc.update(sql,
                empId,
                attendance.getDate() != null ? Date.valueOf(attendance.getDate()) : null,
                attendance.getClock_in() != null ? Time.valueOf(attendance.getClock_in()) : null,
                attendance.getClock_out() != null ? Time.valueOf(attendance.getClock_out()) : null,
                attendance.getStatus() != null ? attendance.getStatus().name() : null,
                attendance.getAttendance_id());
        return attendance;
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE attendance_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}