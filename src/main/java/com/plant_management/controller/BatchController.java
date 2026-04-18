package com.plant_management.controller;

import com.plant_management.dto.BatchRequestDTO;
import com.plant_management.model.Batches;
import com.plant_management.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batches")
@CrossOrigin(origins = "https://grainsync.up.railway.app")

public class BatchController {

    @Autowired
    private BatchService batchService;

    // Save a new batch with logic for updating inventory
    @PostMapping
    public ResponseEntity<?> logBatch(@RequestBody Map<String, Object> payload) {
        try {
            Integer product_id = (Integer) payload.get("product_id");
            Integer employee_id = (Integer) payload.get("employee_id");
            Integer r_storage_unit_id = (Integer) payload.get("r_storage_unit_id");

            Float quantity_used = ((Number) payload.get("quantity_used")).floatValue();
            Float quantity_produced = ((Number) payload.get("quantity_produced")).floatValue();

            String start_time = (String) payload.get("start_time");
            String end_time = (String) payload.get("end_time");

            Batches batch = new Batches();
            batch.setStart_time(start_time);
            batch.setEnd_time(end_time);
            batch.setQuantity_used(quantity_used);
            batch.setQuantity_produced(quantity_produced);

            Batches saved = batchService.saveBatch(batch, employee_id, product_id, r_storage_unit_id);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to log batch: " + e.getMessage());
        }
    }

    // Get all batches
    @GetMapping
    public List<Batches> getAllBatches() {
        return batchService.getAllBatches();
    }

    // Get batch by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBatchById(@PathVariable Integer id) {
        Batches batch = batchService.getBatchById(id);
        if (batch == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(batch);
    }
    @PostMapping("/log")
    public ResponseEntity<?> logBatch(@RequestBody BatchRequestDTO dto) {
        return batchService.logBatch(dto);
    }

    // Delete a batch
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBatch(@PathVariable Integer id) {
        if (batchService.getBatchById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        batchService.deleteBatch(id);
        return ResponseEntity.ok().build();
    }
}
