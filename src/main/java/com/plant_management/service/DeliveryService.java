package com.plant_management.service;

import com.plant_management.model.Delivery;
import com.plant_management.model.Order;
import com.plant_management.dao.DeliveryDao;
import com.plant_management.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryDao deliveryDao;

    @Autowired
    private OrderDao orderDao;

    public List<Delivery> getAllDeliveries() {
        return deliveryDao.findAll();
    }

    public Optional<Delivery> getDeliveryById(Integer id) {
        return deliveryDao.findById(id);
    }

    @Transactional
    public Delivery createDelivery(Delivery delivery) {
        Order order = delivery.getOrder();
        order.setStatus(Order.OrderStatus.valueOf("shipped"));
        orderDao.save(order);

        return deliveryDao.save(delivery);
    }

    @Transactional
    public Delivery updateDelivery(Integer id, Delivery deliveryDetails) {
        Delivery delivery = deliveryDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setVehicle(deliveryDetails.getVehicle());
        delivery.setDriver(deliveryDetails.getDriver());
        delivery.setDepartureTime(deliveryDetails.getDepartureTime());
        delivery.setDeliveryTime(deliveryDetails.getDeliveryTime());

        return deliveryDao.save(delivery);
    }

    @Transactional
    public void markDeliveryAsCompleted(Integer deliveryId) {
        Delivery delivery = deliveryDao.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setDeliveryTime(LocalDateTime.now());
        deliveryDao.save(delivery);

        Order order = delivery.getOrder();
        order.setStatus(Order.OrderStatus.valueOf("delivered"));
        orderDao.save(order);
    }

    @Transactional
    public void cancelDelivery(Integer deliveryId) {
        Delivery delivery = deliveryDao.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        Order order = delivery.getOrder();
        order.setStatus(Order.OrderStatus.valueOf("cancelled"));
        orderDao.save(order);

        deliveryDao.delete(deliveryId);
    }

    public List<Delivery> getPendingDeliveries() {
        // Get all orders that are pending and don't have a delivery record
        List<Order> pendingOrders = orderDao.findByStatus(Order.OrderStatus.valueOf("pending"));
        for (Order order : pendingOrders) {
            if (!deliveryDao.existsByOrder(order)) {
                Delivery newDelivery = new Delivery();
                newDelivery.setOrder(order);
                deliveryDao.save(newDelivery);
            }
        }
        return deliveryDao.findPendingDeliveries();
    }

    public List<Delivery> getCompletedDeliveries() {
        return deliveryDao.findCompletedDeliveries();
    }
}