// java
package com.plant_management.service;

import com.plant_management.dto.OrderProductDTO;
import com.plant_management.dto.OrderRequestDTO;
import com.plant_management.model.*;
import com.plant_management.dao.CustomerDao;
import com.plant_management.dao.OrderDao;
import com.plant_management.dao.OrdersProductsDao;
import com.plant_management.dao.ProductInventoryStorageDao;
import com.plant_management.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductInventoryStorageDao productInventoryStorageDao;

    @Autowired
    private OrdersProductsDao ordersProductsDao;

    @Autowired
    private CustomerDao customersDao;

    @Autowired
    private ProductDao productDao;

    public Order saveOrder(Order order) {
        return orderDao.save(order);
    }

    public List<Order> getAllOrders() {
        return orderDao.findAll();
    }

    public Optional<Order> getOrderById(int orderId) {
        return orderDao.findById(orderId);
    }

    public void deleteOrder(int orderId) {
        orderDao.deleteById(orderId);
    }

    @Transactional
    public void createOrderWithProducts(OrderRequestDTO orderRequest) {
        try {
            Customer customer = customersDao.findById(orderRequest.getCustomer_id())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Integer nextOrderId = orderDao.findMaxOrderId().orElse(0) + 1;

            Order order = new Order();
            order.setOrder_id(nextOrderId);
            order.setCustomer_id(orderRequest.getCustomer_id());
            order.setEmployee_id(orderRequest.getEmployee_id());
            order.setOrder_date(LocalDate.parse(orderRequest.getOrder_date()));
            order.setStatus("pending");
            order.setAddress(customer.getAddress());

            Order savedOrder = orderDao.save(order);

            for (OrderProductDTO productDTO : orderRequest.getProducts()) {
                int productId = productDTO.getProduct_id();
                float quantityOrdered = productDTO.getQuantity();

                // Save order-product mapping
                OrdersProducts op = new OrdersProducts();
                op.setOrder_id(savedOrder.getOrder_id());
                op.setProduct_id(productId);
                op.setQuantity(quantityOrdered);
                ordersProductsDao.save(op);

                // Fetch product and inventory
                Products product = productDao.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                List<ProductInventoryStorage> inventoryList = productInventoryStorageDao.findByProducts(product);
                if (inventoryList.isEmpty()) {
                    throw new RuntimeException("No inventory found for product ID: " + productId);
                }

                // Calculate total available quantity
                float totalAvailable = 0f;
                for (ProductInventoryStorage inv : inventoryList) {
                    totalAvailable += inv.getQuantity_stored();
                }

                if (totalAvailable < quantityOrdered) {
                    throw new RuntimeException("Insufficient stock for product ID: " + productId +
                            ". Requested: " + quantityOrdered + ", Available: " + totalAvailable);
                }

                // Deduct quantity in FIFO order
                float remainingToDeduct = quantityOrdered;
                for (ProductInventoryStorage storage : inventoryList) {
                    float available = storage.getQuantity_stored();
                    if (available >= remainingToDeduct) {
                        storage.setQuantity_stored(available - remainingToDeduct);
                        productInventoryStorageDao.save(storage);
                        break;
                    } else {
                        storage.setQuantity_stored(0f);
                        remainingToDeduct -= available;
                        productInventoryStorageDao.save(storage);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    public List<Order> getPendingOrders() {
        return orderDao.findByStatus("pending");
    }

    public List<Order> getInDeliveryOrders() {
        return orderDao.findByStatus("in_delivery");
    }

    public List<Order> getDeliveredOrders() {
        return orderDao.findByStatus("delivered");
    }

    public List<Order> getCancelledOrders() {
        return orderDao.findByStatus("cancelled");
    }
}