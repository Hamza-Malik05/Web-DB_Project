package com.plant_management.service;

import com.plant_management.model.Attendance;
import com.plant_management.model.Employee;
import com.plant_management.dao.AttendanceRepository;
import com.plant_management.dao.EmployeeRepository;
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

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Mark attendance for a specific employee on a given date.
     */
    public Attendance markAttendance(Integer employeeId, LocalDate date, LocalTime clockIn, LocalTime clockOut) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, date)
                .orElse(new Attendance());

        attendance.setEmployee(employee);
        attendance.setDate(date);
        attendance.setClock_in(clockIn);
        attendance.setClock_out(clockOut);
        attendance.setStatus(clockIn != null ? Attendance.Status.present : Attendance.Status.absent);

        return attendanceRepository.save(attendance);
    }

    /**
     * Retrieve attendance history for a specific employee.
     */
    public List<Attendance> getAttendanceHistory(Integer employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    /**
     * Initialize attendance records for all employees for a specific date.
     */
    @Transactional
    public List<Attendance> initializeAttendanceForDate(LocalDate date) {
        log.info("Initializing attendance records for date: {}", date);

        // First check if we already have records for this date
        List<Attendance> existingRecords = attendanceRepository.findAllByDate(date);
        log.info("Found {} existing records for date {}", existingRecords.size(), date);

        if (!existingRecords.isEmpty()) {
            log.info("Returning existing records for date {}", date);
            return existingRecords;
        }

        // If no records exist, create new ones for all employees
        List<Employee> employees = employeeRepository.findAll();
        log.info("Found {} employees to create attendance records for", employees.size());

        List<Attendance> newRecords = new ArrayList<>();
        for (Employee employee : employees) {
            try {
                // Double check if record exists for this employee and date
                Optional<Attendance> existingRecord = attendanceRepository.findByEmployeeIdAndDate(
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
        List<Attendance> savedRecords = attendanceRepository.saveAll(newRecords);
        log.info("Saved {} new attendance records for date {}", savedRecords.size(), date);
        return savedRecords;
    }

    /**
     * Retrieve attendance records for a specific date.
     */
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findAllByDate(date);
    }

    public Optional<Attendance> getAttendanceById(Integer attendanceId) {
        return attendanceRepository.findById(attendanceId);
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Transactional
    public Attendance markAbsent(Attendance attendance) {
        log.info("Marking employee {} as absent for date {}",
                attendance.getEmployee().getEmployee_id(),
                attendance.getDate());

        // Set attendance status to absent
        attendance.setStatus(Attendance.Status.absent);

        // Get the employee and update their absence count
        Employee employee = attendance.getEmployee();
        employee.setAbsences(employee.getAbsences() + 1);
        employee.setLeaves(employee.getLeaves() - 1);
        employeeRepository.save(employee);

        log.info("Updated employee {} absence count to {} and leaves to {}",
                employee.getEmployee_id(),
                employee.getAbsences(),
                employee.getLeaves());

        // Save and return the updated attendance record
        return attendanceRepository.save(attendance);
    }
}