package com.plant_management.controller;

import com.plant_management.dto.RecordPurchaseRequestDTO;
import com.plant_management.dto.RecordPurchaseResponseDTO;
import com.plant_management.model.Purchase;
import com.plant_management.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:3000")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases() {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Integer id) {
        return purchaseService.getPurchaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(@RequestBody Purchase purchase) {
        Purchase savedPurchase = purchaseService.savePurchase(purchase);
        return ResponseEntity.ok(savedPurchase);
    }

    @PostMapping("/record")
    public ResponseEntity<RecordPurchaseResponseDTO> recordPurchase(@RequestBody RecordPurchaseRequestDTO req) {
        if (req == null || req.getSupplier_id() == null || req.getUnits_bought() == null) {
            return ResponseEntity.badRequest().build();
        }

        BigDecimal total = purchaseService.recordNewPurchase(req.getSupplier_id(), req.getUnits_bought());
        if (total == null) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok(new RecordPurchaseResponseDTO(total));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Integer id, @RequestBody Purchase updatedPurchase) {
        return purchaseService.getPurchaseById(id)
                .map(existing -> {
                    updatedPurchase.setPurchase_id(id);
                    return ResponseEntity.ok(purchaseService.savePurchase(updatedPurchase));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Integer id) {
        if (purchaseService.getPurchaseById(id).isPresent()) {
            purchaseService.deletePurchase(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}