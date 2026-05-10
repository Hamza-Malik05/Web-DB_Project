package com.plant_management.service;

import com.plant_management.dao.PurchaseDao;
import com.plant_management.model.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    public List<Purchase> getAllPurchases() {
        // Returns the raw entity list, preserving the nested 'supplier' object
        return purchaseDao.findAll();
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

    /**
     * Calls DAO wrapper that invokes the database function record_new_purchase(...)
     * Returns the total bill calculated by the DB function.
     */
    public BigDecimal recordNewPurchase(Integer supplierId, BigDecimal unitsBought) {
        return purchaseDao.recordNewPurchaseViaFunction(supplierId, unitsBought);
    }
}