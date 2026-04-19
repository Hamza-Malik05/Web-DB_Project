package com.plant_management.controller;

import com.plant_management.dto.BillRequestDTO;
import com.plant_management.dto.BillResponseDTO;
import com.plant_management.model.Bill;
import com.plant_management.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:3000")


public class BillController {

    private final BillService billService;

    // Constructor injection
    public BillController(BillService billService) {
        this.billService = billService;
    }

    // Get all Bills
    @GetMapping
    public List<BillResponseDTO> getAllBills() {
        return billService.getAllBills();
    }

    // Get Bill by ID
    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Integer id) {
        return billService.getBillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create Bill + Transaction
    @PostMapping("/create")
    public ResponseEntity<String> createBill(@RequestBody BillRequestDTO dto) {
        billService.createBillViaProcedure(dto);
        return ResponseEntity.ok("Bill successfully created.");
    }

    // Update Bill by ID
    @PutMapping("/{id}")
    public ResponseEntity<Bill> updateBill(@PathVariable Integer id, @RequestBody Bill updatedBill) {
        return billService.getBillById(id)
                .map(existing -> {
                    updatedBill.setBill_id(id);
                    return ResponseEntity.ok(billService.saveBill(updatedBill));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete Bill by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Integer id) {
        if (billService.getBillById(id).isPresent()) {
            billService.deleteBill(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
