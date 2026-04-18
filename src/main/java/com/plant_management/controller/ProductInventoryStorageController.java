package com.plant_management.controller;

import com.plant_management.dto.ProductInventoryDTO;
import com.plant_management.model.ProductInventoryStorage;
import com.plant_management.model.Products;
import com.plant_management.dao.ProductRepository;
import com.plant_management.dao.ProductInventoryStorageRepository;
import com.plant_management.service.ProductInventoryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-inventory-storage")
@CrossOrigin(origins = "https://grainsync.up.railway.app")

public class ProductInventoryStorageController {

    @Autowired
    private ProductInventoryStorageService service;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryStorageRepository productInventoryStorageRepository;

    // Get all storage units
    @GetMapping
    public List<ProductInventoryStorage> getAllStorageUnits() {
        return service.getAllUnits();
    }

    // Get storage unit by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStorageUnitById(@PathVariable Integer id) {
        return service.getUnitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get combined product inventory data for frontend
    @GetMapping("/product-inventory")
    public List<ProductInventoryDTO> getProductInventory() {
        return service.getProductInventoryDTOs();
    }

    // Create a new storage unit
    @PostMapping
    public ResponseEntity<?> createStorageUnit(@RequestBody ProductInventoryStorage unit) {
        if (unit.getProducts() != null && unit.getProducts().getProduct_id() != null) {
            Products product = productRepository.findById(unit.getProducts().getProduct_id()).orElse(null);
            if (product == null) return ResponseEntity.badRequest().body("Invalid product_id.");
            unit.setProducts(product);
        }
        return ResponseEntity.ok(service.saveUnit(unit));
    }

    // Update an existing storage unit
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStorageUnit(@PathVariable Integer id, @RequestBody ProductInventoryStorage unit) {
        return service.getUnitById(id).map(existing -> {
            unit.setP_storage_unit_id(id);

            if (unit.getProducts() != null && unit.getProducts().getProduct_id() != null) {
                Products product = productRepository.findById(unit.getProducts().getProduct_id()).orElse(null);
                if (product == null) return ResponseEntity.badRequest().body("Invalid product_id.");
                unit.setProducts(product);
            }

            return ResponseEntity.ok(service.saveUnit(unit));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a storage unit
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStorageUnit(@PathVariable Integer id) {
        if (service.getUnitById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteUnit(id);
        return ResponseEntity.ok().build();
    }
}
