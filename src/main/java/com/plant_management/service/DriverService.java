package com.plant_management.service;

import com.plant_management.model.Driver;
import com.plant_management.dao.DriverDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverDao driverDao;

    public List<Driver> getAllDrivers() {
        return driverDao.findAll();
    }

    public Optional<Driver> getDriverById(int id) {
        return driverDao.findById(id);
    }

    public Driver saveDriver(Driver driver) {
        return driverDao.save(driver);
    }

    public void deleteDriver(int id) {
        driverDao.deleteById(id);
    }
}