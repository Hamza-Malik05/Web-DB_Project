package com.plant_management.controller;

import com.plant_management.model.Salaries;
import com.plant_management.service.SalaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@CrossOrigin(origins = "*") // Updated to allow any origin during debugging, or specify your port
@Slf4j
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @PostMapping
    public ResponseEntity<?> createSalary(
            @RequestParam Integer employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam BigDecimal baseAmount,
            @RequestParam BigDecimal bonus,
            @RequestParam BigDecimal fine,
            @RequestParam Integer accountantId,
            @RequestParam String paymentMethod) {
        try {
            log.info("Request to create salary for employee: {} on date: {}", employeeId, date);
            Salaries salary = salaryService.createSalary(employeeId, date, baseAmount, bonus, fine, accountantId, paymentMethod);
            return ResponseEntity.status(HttpStatus.CREATED).body(salary);
        } catch (RuntimeException e) {
            log.error("Failed to create salary: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{salaryId}")
    public ResponseEntity<?> updateSalary(
            @PathVariable Integer salaryId,
            @RequestParam BigDecimal bonus,
            @RequestParam BigDecimal fine) {
        try {
            log.info("Request to update salary ID: {}", salaryId);
            Salaries salary = salaryService.updateSalary(salaryId, bonus, fine);
            return ResponseEntity.ok(salary);
        } catch (RuntimeException e) {
            log.error("Failed to update salary: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Salaries>> getAllSalaries() {
        return ResponseEntity.ok(salaryService.getAllSalaries());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Salaries>> getSalariesByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        log.info("Fetching salaries for date: {}", date);
        return ResponseEntity.ok(salaryService.getSalariesByDate(date));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Salaries>> getSalariesByEmployee(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(salaryService.getSalariesByEmployee(employeeId));
    }

    @GetMapping("/{salaryId}")
    public ResponseEntity<Salaries> getSalaryById(@PathVariable Integer salaryId) {
        try {
            Salaries salary = salaryService.getSalaryById(salaryId);
            return ResponseEntity.ok(salary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}