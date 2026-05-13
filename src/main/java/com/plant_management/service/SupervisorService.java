package com.plant_management.service;

import com.plant_management.model.Supervisor;
import com.plant_management.dao.SupervisorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupervisorService {

    @Autowired
    private SupervisorDao supervisorDao;

    public Supervisor createSupervisor(Supervisor supervisor) {
        return supervisorDao.save(supervisor);
    }

    public Supervisor getSupervisorById(Integer id) {
        return supervisorDao.findById(id).orElse(null);
    }

    public List<Supervisor> getAllSupervisors() {
        return supervisorDao.findAll();
    }

    public Supervisor updateSupervisor(Integer id, Supervisor updatedSupervisor) {
        Optional<Supervisor> optional = supervisorDao.findById(id);
        if (optional.isPresent()) {
            updatedSupervisor.setSupervisor_id(id);
            return supervisorDao.save(updatedSupervisor);
        } else {
            return null;
        }
    }

    public void deleteSupervisor(Integer id) {
        supervisorDao.deleteById(id);
    }
}