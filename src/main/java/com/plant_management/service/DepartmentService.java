package com.plant_management.service;

import com.plant_management.model.Department;
import com.plant_management.dao.DepartmentDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentDao departmentDao;

    public DepartmentService(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public List<Department> getAllDepartments() {
        return departmentDao.findAll();
    }

    public Department getDepartmentById(Integer id) {
        return departmentDao.findById(id).orElse(null);
    }

    public Department saveDepartment(Department department) {
        return departmentDao.insert(department);
    }

    public void deleteDepartment(Integer id) {
        departmentDao.delete(id);
    }
}
