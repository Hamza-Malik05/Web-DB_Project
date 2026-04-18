package com.plant_management.service;

import com.plant_management.dto.OrderProductDTO;
import com.plant_management.dto.OrderRequestDTO;
import com.plant_management.model.*;
import com.plant_management.dao.CustomerRepository;
import com.plant_management.dao.OrderRepository;
import com.plant_management.dao.OrdersProductsRepository;
import com.plant_management.dao.ProductInventoryStorageRepository;
import com.plant_management.dao.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductInventoryStorageRepository productInventoryStorageRepository;

    @Autowired
    private OrdersProductsRepository ordersProductsRepository;

    @Autowired
    private CustomerRepository customersRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(int orderId) {
        return orderRepository.findById(orderId);
    }

    public void deleteOrder(int orderId) {
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void createOrderWithProducts(OrderRequestDTO orderRequest) {
        try {
            Customer customer = customersRepository.findById(orderRequest.getCustomer_id())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Integer nextOrderId = orderRepository.findMaxOrderId() + 1;

            Order order = new Order();
            order.setOrder_id(nextOrderId);
            order.setCustomer_id(orderRequest.getCustomer_id());
            order.setEmployee_id(orderRequest.getEmployee_id());
            order.setOrder_date(LocalDate.parse(orderRequest.getOrder_date()));
            order.setStatus("pending");
            order.setAddress(customer.getAddress());

            Order savedOrder = orderRepository.save(order);

            for (OrderProductDTO productDTO : orderRequest.getProducts()) {
                int productId = productDTO.getProduct_id();
                float quantityOrdered = productDTO.getQuantity();

                // Save order-product mapping
                OrdersProducts op = new OrdersProducts();
                op.setOrder_id(savedOrder.getOrder_id());
                op.setProduct_id(productId);
                op.setQuantity(quantityOrdered);
                ordersProductsRepository.save(op);

                // Fetch product and inventory
                Products product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                List<ProductInventoryStorage> inventoryList = productInventoryStorageRepository.findByProducts(product);
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
                        productInventoryStorageRepository.save(storage);
                        break;
                    } else {
                        storage.setQuantity_stored(0f);
                        remainingToDeduct -= available;
                        productInventoryStorageRepository.save(storage);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus("pending");
    }

    public List<Order> getInDeliveryOrders() {
        return orderRepository.findByStatus("in_delivery");
    }

    public List<Order> getDeliveredOrders() {
        return orderRepository.findByStatus("delivered");
    }

    public List<Order> getCancelledOrders() {
        return orderRepository.findByStatus("cancelled");
    }
}
