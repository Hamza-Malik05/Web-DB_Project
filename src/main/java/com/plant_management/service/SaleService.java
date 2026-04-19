package com.plant_management.service;

import com.plant_management.model.Sale;
import com.plant_management.dao.SaleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    @Autowired
    private SaleDao saleDao;

    public List<Sale> getAllSales() {
        return saleDao.findAll();
    }

    public Optional<Sale> getSaleById(int sale_id) {
        return saleDao.findById(sale_id);
    }

    public Sale addSale(Sale sale) {
        return saleDao.save(sale);
    }

    public void deleteSale(int sale_id) {
        saleDao.deleteById(sale_id);
    }

    public Sale updateSale(Sale sale) {
        return saleDao.save(sale);
    }
}
