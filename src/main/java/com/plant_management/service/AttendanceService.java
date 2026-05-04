package com.plant_management.service;

import com.plant_management.model.Attendance;
import com.plant_management.model.Employee;
import com.plant_management.dao.AttendanceDao;
import com.plant_management.dao.EmployeeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceDao attendanceDao;
    private final EmployeeDao employeeDao;

    /**
     * Mark attendance for a specific employee on a given date.
     */
    public Attendance markAttendance(Integer employeeId, LocalDate date, LocalTime clockIn, LocalTime clockOut) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Attendance attendance = attendanceDao.findByEmployeeIdAndDate(employeeId, date)
                .orElse(new Attendance());

        attendance.setEmployee(employee);
        attendance.setDate(date);
        attendance.setClock_in(clockIn);
        attendance.setClock_out(clockOut);
        attendance.setStatus(clockIn != null ? Attendance.Status.present : Attendance.Status.absent);

        return attendanceDao.save(attendance);
    }

    /**
     * Retrieve attendance history for a specific employee.
     */
    public List<Attendance> getAttendanceHistory(Integer employeeId) {
        return attendanceDao.findByEmployeeId(employeeId);
    }

    /**
     * Initialize attendance records for all employees for a specific date.
     */
    @Transactional
    public List<Attendance> initializeAttendanceForDate(LocalDate date) {
        log.info("Initializing attendance records for date: {}", date);

        // First check if we already have records for this date
        List<Attendance> existingRecords = attendanceDao.findAllByDate(date);
        log.info("Found {} existing records for date {}", existingRecords.size(), date);

        if (!existingRecords.isEmpty()) {
            log.info("Returning existing records for date {}", date);
            return existingRecords;
        }

        // If no records exist, create new ones for all employees
        List<Employee> employees = employeeDao.findAll();
        log.info("Found {} employees to create attendance records for", employees.size());

        List<Attendance> newRecords = new ArrayList<>();
        for (Employee employee : employees) {
            try {
                // Double check if record exists for this employee and date
                Optional<Attendance> existingRecord = attendanceDao.findByEmployeeIdAndDate(
                        employee.getEmployee_id(), date);

                if (existingRecord.isPresent()) {
                    log.info("Record already exists for employee {} on date {}",
                            employee.getEmployee_id(), date);
                    continue;
                }

                Attendance attendance = new Attendance();
                attendance.setEmployee(employee);
                attendance.setDate(date);
                attendance.setStatus(null);
                newRecords.add(attendance);
                log.info("Created new attendance record for employee {} on date {}",
                        employee.getEmployee_id(), date);
            } catch (Exception e) {
                log.error("Error creating attendance record for employee {} on date {}: {}",
                        employee.getEmployee_id(), date, e.getMessage());
            }
        }

        // Save all new records at once
        List<Attendance> savedRecords = new ArrayList<>();
        for (Attendance rec : newRecords) {
            try {
                savedRecords.add(attendanceDao.save(rec));
            } catch (Exception e) {
                log.error("Failed to save attendance for employee {} on date {}: {}",
                        rec.getEmployee() != null ? rec.getEmployee().getEmployee_id() : "unknown",
                        rec.getDate(),
                        e.getMessage());
            }
        }
        log.info("Saved {} new attendance records for date {}", savedRecords.size(), date);
        return savedRecords;
    }

    /**
     * Retrieve attendance records for a specific date.
     */
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceDao.findAllByDate(date);
    }

    public Optional<Attendance> getAttendanceById(Integer attendanceId) {
        return attendanceDao.findById(attendanceId);
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceDao.save(attendance);
    }

    @Transactional
    public Attendance markAbsent(Attendance attendance) {
        Employee employee = attendance.getEmployee();

        // 1. Guard against a null employee (prevents NPE on employee.getEmployee_id())
        if (employee == null) {
            throw new RuntimeException("Cannot mark absent: No employee attached to this attendance record.");
        }

        log.info("Marking employee {} as absent for date {}",
                employee.getEmployee_id(),
                attendance.getDate());

        // Set attendance status to absent
        attendance.setStatus(Attendance.Status.absent);

        // 2. Safely handle potential nulls to prevent unboxing NullPointerExceptions
        int currentAbsences = employee.getAbsences() != null ? employee.getAbsences() : 0;
        int currentLeaves = employee.getLeaves() != null ? employee.getLeaves() : 21; // 21 is your DB default

        employee.setAbsences(currentAbsences + 1);
        employee.setLeaves(currentLeaves - 1);

        employeeDao.update(employee);

        log.info("Updated employee {} absence count to {} and leaves to {}",
                employee.getEmployee_id(),
                employee.getAbsences(),
                employee.getLeaves());

        // Save and return the updated attendance record
        return attendanceDao.save(attendance);
    }
}