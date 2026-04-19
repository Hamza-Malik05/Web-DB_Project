package com.plant_management.service;

import com.plant_management.dto.PurchaseResponseDTO;
import com.plant_management.model.Purchase;
import com.plant_management.dao.PurchaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    public List<PurchaseResponseDTO> getAllPurchases() {
        List<Purchase> purchases = purchaseDao.findAll();

        return purchases.stream()
                .map(purchase -> new PurchaseResponseDTO(
                        purchase.getPurchase_id(),
                        purchase.getSupplier().getName(),
                        purchase.getDate_of_purchase(),
                        purchase.getDelivery_date(),
                        purchase.getUnit_of_measurement(),
                        purchase.getUnits_bought(),
                        purchase.getPrice_per_unit(),
                        purchase.getUnits_bought() * purchase.getPrice_per_unit()
                ))
                .collect(Collectors.toList());
    }


    public Optional<Purchase> getPurchaseById(Integer id) {
        return purchaseDao.findById(id);
    }

    public Purchase savePurchase(Purchase purchase) {
        return purchaseDao.save(purchase);
    }

    public void deletePurchase(Integer id) {
        purchaseDao.deleteById(id);
    }
}