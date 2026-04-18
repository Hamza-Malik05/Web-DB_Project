package com.plant_management.controller;

import com.plant_management.model.Department;
import com.plant_management.model.Employee;
import com.plant_management.dao.DepartmentDao;
import com.plant_management.dao.EmployeeDao;
import com.plant_management.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000")


public class EmployeeController {
    private final EmployeeDao employeeDao;
    private final EmployeeService employeeService;
    private final DepartmentDao departmentDao;

    public EmployeeController(EmployeeService employeeService, DepartmentDao departmentDao,EmployeeDao employeeDao) {
        this.employeeService = employeeService;
        this.departmentDao = departmentDao;
        this.employeeDao= employeeDao;
    }

    @GetMapping("/production")
    public List<Employee> getProductionEmployees() {
        return employeeService.getProductionEmployees();
    }

    @GetMapping("/sales")
    public List<Employee> getSalesReps() {
        return employeeService.getSalesReps();
    }
    @GetMapping("/debug/unregistered-employees")
    public ResponseEntity<List<Employee>> debugUnregistered() {
        List<Employee> result = employeeDao.findEmployeesNotRegisteredAsUsers();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unregistered")
    public ResponseEntity<List<Employee>> getUnregisteredEmployees() {
        List<Employee> employees = employeeService.getUnregisteredEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        employee.setAbsences(0);
        employee.setLeaves(21);

        if (employee.getDept_id() != null) {
            Department department = departmentDao.findById(employee.getDept_id())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID"));
        }

        Employee savedEmployee = employeeService.saveEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Integer id, @RequestBody Employee updatedEmployee) {
        try {
            if (updatedEmployee.getDept_id() != null) {
                Department department = departmentDao.findById(updatedEmployee.getDept_id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID"));
            }
            Employee employee = employeeService.updateEmployee(id, updatedEmployee);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update employee: " + e.getMessage());
        }
    }

    @GetMapping("/{id}") // ❗This must be last to avoid matching everything
    public ResponseEntity<?> getEmployeeById(@PathVariable Integer id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
