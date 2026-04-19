package com.plant_management.service;

import com.plant_management.dto.BatchRequestDTO;
import com.plant_management.model.*;
import com.plant_management.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    @Autowired
    private BatchDao batchDao; // replaced BatchRepository

    @Autowired
    private EmployeeDao employeeDao; // use DAO or repository present in project

    @Autowired
    private ProductDao productDao; // keep existing repository if present

    @Autowired
    private RawMaterialInventoryStorageDao rawMaterialInventoryStorageDao;

    @Autowired
    private ProductInventoryStorageDao productInventoryStorageDao;

    public Batches saveBatch(Batches batch, Integer employee_id, Integer product_id, Integer r_storage_unit_id) {
        // unchanged business logic before saving; at the end use batchDao.save(...)
        Employee employee = employeeDao.findById(employee_id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Products product = productDao.findById(product_id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        batch.setEmployee(employee);
        batch.setProduct(product);

        RawMaterialInventoryStorage rawStorage = rawMaterialInventoryStorageDao.findById(r_storage_unit_id)
                .orElseThrow(() -> new RuntimeException("Raw material storage unit not found"));

        if (rawStorage.getQuantity_stored() < batch.getQuantity_used()) {
            throw new RuntimeException("Not enough raw material in storage");
        }

        rawStorage.setQuantity_stored(rawStorage.getQuantity_stored() - batch.getQuantity_used());
        rawMaterialInventoryStorageDao.save(rawStorage);

        List<ProductInventoryStorage> productUnits = productInventoryStorageDao
                .findByProducts(product);

        if (productUnits.isEmpty()) {
            throw new RuntimeException("No product storage units found for this product.");
        }

        ProductInventoryStorage suitableUnit = productUnits.stream()
                .filter(unit -> unit.getCapacity() - unit.getQuantity_stored() >= batch.getQuantity_produced())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No product storage unit has enough capacity."));

        suitableUnit.setQuantity_stored(suitableUnit.getQuantity_stored() + batch.getQuantity_produced());
        productInventoryStorageDao.save(suitableUnit);

        return batchDao.save(batch);
    }

    public ResponseEntity<?> logBatch(BatchRequestDTO dto) {
        Optional<Employee> employeeOpt = employeeDao.findById(Math.toIntExact(dto.getEmployee_id()));
        Optional<Products> productOpt = productDao.findById(Math.toIntExact(dto.getProduct_id()));

        if (employeeOpt.isEmpty() || productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid employee or product ID.");
        }

        RawMaterialInventoryStorage rawStorage = rawMaterialInventoryStorageDao.findById(Math.toIntExact(dto.getR_storage_unit_id()))
                .orElse(null);
        ProductInventoryStorage productStorage = productInventoryStorageDao.findById(Math.toIntExact(dto.getP_storage_unit_id()))
                .orElse(null);

        if (rawStorage == null || productStorage == null) {
            return ResponseEntity.badRequest().body("Invalid storage unit.");
        }

        if (rawStorage.getQuantity_stored() < dto.getQuantity_used()) {
            return ResponseEntity.badRequest().body("Insufficient raw material in storage.");
        }

        rawStorage.setQuantity_stored(rawStorage.getQuantity_stored() - dto.getQuantity_used());
        productStorage.setQuantity_stored(productStorage.getQuantity_stored() + dto.getQuantity_produced());
        rawMaterialInventoryStorageDao.save(rawStorage);
        productInventoryStorageDao.save(productStorage);

        Batches batch = new Batches();
        batch.setEmployee(employeeOpt.get());
        batch.setProduct(productOpt.get());
        batch.setQuantity_used(dto.getQuantity_used());
        batch.setQuantity_produced(dto.getQuantity_produced());

        return ResponseEntity.ok(batchDao.save(batch));
    }

    public List<Batches> getAllBatches() {
        return batchDao.findAll();
    }

    public Batches getBatchById(Integer id) {
        return batchDao.findById(id).orElse(null);
    }

    public void deleteBatch(Integer id) {
        batchDao.deleteById(id);
    }

    public List<Products> getAllProducts() {
        return productDao.findAll();
    }
}