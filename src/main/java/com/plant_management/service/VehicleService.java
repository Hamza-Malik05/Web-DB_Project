package com.plant_management.service;

import com.plant_management.model.Vehicle;
import com.plant_management.dao.VehicleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleDao vehicleDao;

    public List<Vehicle> getAllVehicles() {
        return vehicleDao.findAll();
    }

    public Vehicle getVehicleById(Integer id) {
        return vehicleDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleDao.save(vehicle);
    }

    public Vehicle updateVehicle(Integer id, Vehicle vehicleDetails) {
        Vehicle vehicle = vehicleDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setType(vehicleDetails.getType());
        vehicle.setLicense_plate(vehicleDetails.getLicense_plate());
        vehicle.setModel(vehicleDetails.getModel());
        vehicle.setCapacity(vehicleDetails.getCapacity());
        vehicle.setStatus(vehicleDetails.getStatus());

        return vehicleDao.save(vehicle);
    }

    public void deleteVehicle(Integer id) {
        vehicleDao.deleteById(id);
    }

    public List<Vehicle> getActiveVehicles() {
        return vehicleDao.findByStatus(Vehicle.Status.active);
    }
}