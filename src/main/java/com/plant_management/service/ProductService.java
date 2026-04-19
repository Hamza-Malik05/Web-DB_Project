package com.plant_management.service;

import com.plant_management.model.Products;
import com.plant_management.dao.ProductDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductDao productDao;

    // Constructor injection is highly recommended over @Autowired field injection
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Products> getAllProducts() {
        // Fixed: now correctly calls the dao instead of the old repository
        return productDao.findAll();
    }

    // --- Standard CRUD Methods ---

    public Optional<Products> getProductById(Integer id) {
        return productDao.findById(id);
    }

    public Products saveProduct(Products product) {
        return productDao.save(product);
    }

    public void deleteProduct(Integer id) {
        productDao.deleteById(id);
    }
}