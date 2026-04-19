package com.plant_management.service;

import com.plant_management.model.Supplier;
import com.plant_management.dao.SupplierDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierDao supplierDao;

    public SupplierService(SupplierDao supplierDao) {
        this.supplierDao = supplierDao;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierDao.findAll();
    }

    public Supplier getSupplierById(Integer id) {
        return supplierDao.findById(id).orElseThrow(
                () -> new RuntimeException("Supplier not found with ID: " + id)
        );
    }

    public Supplier saveSupplier(Supplier supplier) {
        return supplierDao.save(supplier);
    }

    public Supplier updateSupplier(Integer id, Supplier supplierDetails) {
        Supplier existingSupplier = getSupplierById(id);
        existingSupplier.setName(supplierDetails.getName());
        existingSupplier.setEmail(supplierDetails.getEmail());
        existingSupplier.setPhone(supplierDetails.getPhone());
        existingSupplier.setAddress(supplierDetails.getAddress());
        existingSupplier.setCity(supplierDetails.getCity());
        return supplierDao.save(existingSupplier);
    }

    public void deleteSupplierById(Integer id) {
        supplierDao.deleteById(id);
    }
}