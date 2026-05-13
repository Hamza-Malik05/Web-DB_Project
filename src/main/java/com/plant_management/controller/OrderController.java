package com.plant_management.controller;

import com.plant_management.dao.ProductDao;
import com.plant_management.dto.OrderRequestDTO;
import com.plant_management.model.Driver;
import com.plant_management.model.Order;
import com.plant_management.model.Products;
import com.plant_management.service.BatchService;
import com.plant_management.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")


public class OrderController {

    @Autowired
    private OrderService orderService;
    private final BatchService batchService;

    // Inject it here!
    public OrderController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDTO request) {
        orderService.createOrderWithProducts(request);
        return ResponseEntity.ok("Order placed successfully");
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getDriverById(@PathVariable Integer id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(order))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable int orderId) {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Products>> getAllProducts() {
        List<Products> products = batchService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/status/pending")
    public List<Order> getPendingOrders() {
        return orderService.getPendingOrders();
    }

    @GetMapping("/status/in-delivery")
    public List<Order> getInDeliveryOrders() {
        return orderService.getInDeliveryOrders();
    }

    @GetMapping("/status/delivered")
    public List<Order> getDeliveredOrders() {
        return orderService.getDeliveredOrders();
    }

    @GetMapping("/status/cancelled")
    public List<Order> getCancelledOrders() {
        return orderService.getCancelledOrders();
    }
}