package com.plant_management.service;

import com.plant_management.model.Delivery;
import com.plant_management.model.Order;
import com.plant_management.dao.DeliveryRepository;
import com.plant_management.dao.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Optional<Delivery> getDeliveryById(Integer id) {
        return deliveryRepository.findById(id);
    }

    @Transactional
    public Delivery createDelivery(Delivery delivery) {
        Order order = delivery.getOrder();
        order.setStatus("in_delivery");
        orderRepository.save(order);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery updateDelivery(Integer id, Delivery deliveryDetails) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setVehicle(deliveryDetails.getVehicle());
        delivery.setDriver(deliveryDetails.getDriver());
        delivery.setDepartureTime(deliveryDetails.getDepartureTime());
        delivery.setDeliveryTime(deliveryDetails.getDeliveryTime());

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public void markDeliveryAsCompleted(Integer deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setDeliveryTime(LocalDateTime.now());
        deliveryRepository.save(delivery);

        Order order = delivery.getOrder();
        order.setStatus("delivered");
        orderRepository.save(order);
    }

    @Transactional
    public void cancelDelivery(Integer deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        Order order = delivery.getOrder();
        order.setStatus("cancelled");
        orderRepository.save(order);

        deliveryRepository.delete(delivery);
    }

    public List<Delivery> getPendingDeliveries() {
        // Get all orders that are pending and don't have a delivery record
        List<Order> pendingOrders = orderRepository.findByStatus("pending");
        for (Order order : pendingOrders) {
            if (!deliveryRepository.existsByOrder(order)) {
                Delivery newDelivery = new Delivery();
                newDelivery.setOrder(order);
                deliveryRepository.save(newDelivery);
            }
        }
        return deliveryRepository.findByDeliveryTimeIsNull();
    }

    public List<Delivery> getCompletedDeliveries() {
        return deliveryRepository.findByDeliveryTimeIsNotNull();
    }
}