package com.plant_management.service;

import com.plant_management.model.Accountant;
import com.plant_management.dao.AccountantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountantService {

    @Autowired
    private AccountantDao accountantDao;

    public List<Accountant> getAllAccountants() {
        return accountantDao.findAll();
    }

    public Optional<Accountant> getAccountantById(int id) {
        return accountantDao.findById(id);
    }

    public Accountant saveAccountant(Accountant accountant) {
        return accountantDao.save(accountant);
    }

    public void deleteAccountant(int id) {
        accountantDao.deleteById(id);
    }
}