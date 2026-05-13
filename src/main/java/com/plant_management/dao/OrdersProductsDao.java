package com.plant_management.dao;

import com.plant_management.model.OrdersProducts;
import com.plant_management.model.OrdersProductsId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrdersProductsDao {

    private final JdbcTemplate jdbc;

    public OrdersProductsDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<OrdersProducts> ORDERS_PRODUCTS_ROW_MAPPER = (rs, rowNum) -> {
        OrdersProducts op = new OrdersProducts();
        op.setOrder_id(rs.getInt("order_id"));
        op.setProduct_id(rs.getInt("product_id"));
        op.setQuantity(rs.getFloat("quantity"));
        return op;
    };

    public List<OrdersProducts> findAll() {
        String sql = "SELECT order_id, product_id, quantity FROM orders_products";
        return jdbc.query(sql, ORDERS_PRODUCTS_ROW_MAPPER);
    }

    public Optional<OrdersProducts> findById(OrdersProductsId id) {
        if (id == null || id.getOrder_id() == null || id.getProduct_id() == null) {
            return Optional.empty();
        }

        String sql = "SELECT order_id, product_id, quantity FROM orders_products WHERE order_id = ? AND product_id = ?";
        try {
            OrdersProducts op = jdbc.queryForObject(sql, ORDERS_PRODUCTS_ROW_MAPPER, id.getOrder_id(), id.getProduct_id());
            return Optional.ofNullable(op);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public OrdersProducts insert(OrdersProducts ordersProducts) {
        String sql = "INSERT INTO orders_products (order_id, product_id, quantity) VALUES (?, ?, ?)";
        jdbc.update(sql, ordersProducts.getOrder_id(), ordersProducts.getProduct_id(), ordersProducts.getQuantity());
        return ordersProducts;
    }

    public OrdersProducts save(OrdersProducts ordersProducts) {
        // Create an ID object to check if this record already exists
        OrdersProductsId id = new OrdersProductsId(ordersProducts.getOrder_id(), ordersProducts.getProduct_id());

        if (existsById(id)) {
            // Update the quantity if the record exists
            String sql = "UPDATE orders_products SET quantity = ? WHERE order_id = ? AND product_id = ?";
            jdbc.update(sql, ordersProducts.getQuantity(), ordersProducts.getOrder_id(), ordersProducts.getProduct_id());
            return ordersProducts;
        } else {
            // Insert if it does not exist
            return insert(ordersProducts);
        }
    }

    public void deleteById(OrdersProductsId id) {
        if (id == null || id.getOrder_id() == null || id.getProduct_id() == null) return;

        String sql = "DELETE FROM orders_products WHERE order_id = ? AND product_id = ?";
        jdbc.update(sql, id.getOrder_id(), id.getProduct_id());
    }

    public boolean existsById(OrdersProductsId id) {
        if (id == null || id.getOrder_id() == null || id.getProduct_id() == null) return false;

        String sql = "SELECT COUNT(*) FROM orders_products WHERE order_id = ? AND product_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id.getOrder_id(), id.getProduct_id());
        return count != null && count > 0;
    }
}