package com.plant_management.service;

import com.plant_management.model.Employee;
import com.plant_management.dao.EmployeeDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAllEmployees() {
        return employeeDao.findAll();
    }

    public Employee saveEmployee(Employee employee) {
        return employeeDao.insert(employee);
    }

    public List<Employee> getUnregisteredEmployees() {
        return employeeDao.findEmployeesNotRegisteredAsUsers();
    }
    public void deleteEmployee(Integer id) {
        employeeDao.delete(id);
    }

    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeDao.findById(id);
    }

    public Employee updateEmployee(Integer id, Employee updatedEmployee) {
        // Ensure the employee exists
        employeeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Directly save the updated employee with the provided ID
        updatedEmployee.setEmployee_id(id); // Ensure the ID is set
        int rows = employeeDao.update(updatedEmployee);
        if (rows == 0) {
            throw new RuntimeException("Failed to update employee");
        }
        return updatedEmployee;
    }

    public List<Employee> getProductionEmployees() {
        return employeeDao.findEmployeesInProduction();
    }

    public List<Employee> getSalesReps() {
        return employeeDao.findSalesReps();
    }
}