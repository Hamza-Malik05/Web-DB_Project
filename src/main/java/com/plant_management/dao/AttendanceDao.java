package com.plant_management.dao;

import com.plant_management.model.Attendance;
import com.plant_management.model.Employee; // NEW: Imported Employee model
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
public class  AttendanceDao {

    private final JdbcTemplate jdbc;

    // NEW: A base SQL query that joins the employee table to fetch all necessary data in one trip.
    private final String BASE_SELECT_SQL =
            "SELECT a.attendance_id, a.employee_id, a.date, a.clock_in, a.clock_out, a.status, " +
                    "e.dept_id, e.first_name, e.last_name, e.date_of_birth, e.cnic, e.email, " +
                    "e.designation, e.address, e.gender, e.absences, e.leaves " +
                    "FROM attendance a " +
                    "LEFT JOIN employee e ON a.employee_id = e.employee_id ";

    public AttendanceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Attendance> ATTENDANCE_ROW_MAPPER = (rs, rowNum) -> {
        Attendance a = new Attendance();
        a.setAttendance_id(rs.getObject("attendance_id") != null ? rs.getInt("attendance_id") : null);

        // NEW: Check if there is an employee attached to this attendance record
        // If so, map all the employee attributes directly from the joined ResultSet
        if (rs.getObject("employee_id") != null) {
            Employee employee = new Employee();
            employee.setEmployee_id(rs.getInt("employee_id"));
            employee.setDept_id(rs.getObject("dept_id") != null ? rs.getInt("dept_id") : null);
            employee.setFirst_name(rs.getString("first_name"));
            employee.setLast_name(rs.getString("last_name"));

            Date dob = rs.getDate("date_of_birth");
            if (dob != null) {
                employee.setDate_of_birth(dob);
            }

            employee.setCnic(rs.getString("cnic"));
            employee.setEmail(rs.getString("email"));
            employee.setDesignation(rs.getString("designation"));
            employee.setAddress(rs.getString("address"));

            // NEW: Correctly mapping the Gender Enum safely
            String genderStr = rs.getString("gender");
            if (genderStr != null) {
                try {
                    // Assuming your Enum is Employee.Gender or similar
                    employee.setGender(Employee.Gender.valueOf(genderStr));
                } catch (IllegalArgumentException ex) {
                    employee.setGender(null);
                }
            } else {
                employee.setGender(null);
            }

            employee.setAbsences(rs.getObject("absences") != null ? rs.getInt("absences") : null);
            employee.setLeaves(rs.getObject("leaves") != null ? rs.getInt("leaves") : null);

            a.setEmployee(employee);
        } else {
            a.setEmployee(null);
        }

        // Original Attendance mapping intact
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
        // NEW: Appended WHERE clause to the joined base query
        String sql = BASE_SELECT_SQL + "WHERE a.attendance_id = ?";
        try {
            Attendance a = jdbc.queryForObject(sql, ATTENDANCE_ROW_MAPPER, id);
            return Optional.ofNullable(a);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Attendance> findByEmployeeIdAndDate(Integer employeeId, LocalDate date) {
        // NEW: Appended WHERE clause to the joined base query
        String sql = BASE_SELECT_SQL + "WHERE a.employee_id = ? AND a.date = ?";
        try {
            Attendance a = jdbc.queryForObject(sql, ATTENDANCE_ROW_MAPPER, employeeId, Date.valueOf(date));
            return Optional.ofNullable(a);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Attendance> findByEmployeeId(Integer employeeId) {
        // NEW: Appended WHERE clause to the joined base query
        String sql = BASE_SELECT_SQL + "WHERE a.employee_id = ?";
        return jdbc.query(sql, ATTENDANCE_ROW_MAPPER, employeeId);
    }

    public List<Attendance> findAllByDate(LocalDate date) {
        // NEW: Appended WHERE clause to the joined base query
        String sql = BASE_SELECT_SQL + "WHERE a.date = ?";
        return jdbc.query(sql, ATTENDANCE_ROW_MAPPER, Date.valueOf(date));
    }

    public List<Attendance> findAll() {
        // NEW: Uses the joined base query
        return jdbc.query(BASE_SELECT_SQL, ATTENDANCE_ROW_MAPPER);
    }

    public Attendance insert(Attendance attendance) {
        // Unchanged: Insert logic only needs the employee_id, which you already extract
        final String sql = "INSERT INTO attendance (employee_id, date, clock_in, clock_out, status) VALUES (?, ?, ?, ?, ?::attendance_status)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"attendance_id"});
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
        // Unchanged
        if (attendance.getAttendance_id() == null) {
            return insert(attendance);
        }
        String sql = "UPDATE attendance SET employee_id = ?, date = ?, clock_in = ?, clock_out = ?, status = ?::attendance_status WHERE attendance_id = ?";
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
        // Unchanged
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        // Unchanged
        String sql = "SELECT COUNT(*) FROM attendance WHERE attendance_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}