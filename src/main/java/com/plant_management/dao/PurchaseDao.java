package com.plant_management.dao;

import com.plant_management.model.Purchase;
import com.plant_management.model.Supplier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class PurchaseDao {

    private final JdbcTemplate jdbc;

    public PurchaseDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Purchase> PURCHASE_ROW_MAPPER = (rs, rowNum) -> {
        Purchase p = new Purchase();
        p.setPurchase_id(rs.getInt("purchase_id"));

        // Creating a dummy Supplier object to hold the foreign key ID
        // Assuming your Supplier class has a 'setSupplier_id' method. Change if named differently!
        int supplierId = rs.getInt("supplier_id");
        if (!rs.wasNull()) {
            Supplier supplier = new Supplier();
            supplier.setSupplier_id(supplierId);
            p.setSupplier(supplier);
        } else {
            p.setSupplier(null);
        }

        // Safely map SQL Dates to Java LocalDates
        if (rs.getDate("date_of_purchase") != null) {
            p.setDate_of_purchase(rs.getDate("date_of_purchase").toLocalDate());
        }
        if (rs.getDate("delivery_date") != null) {
            p.setDelivery_date(rs.getDate("delivery_date").toLocalDate());
        }

        p.setUnit_of_measurement(rs.getString("unit_of_measurement"));

        // Use getObject for Floats to avoid primitive 0.0 defaults if the DB value is NULL
        p.setUnits_bought(rs.getObject("units_bought", Float.class));
        p.setPrice_per_unit(rs.getObject("price_per_unit", Float.class));

        return p;
    };

    public List<Purchase> findAll() {
        String sql = "SELECT purchase_id, supplier_id, date_of_purchase, delivery_date, unit_of_measurement, units_bought, price_per_unit FROM purchase";
        return jdbc.query(sql, PURCHASE_ROW_MAPPER);
    }

    public Optional<Purchase> findById(Integer id) {
        String sql = "SELECT purchase_id, supplier_id, date_of_purchase, delivery_date, unit_of_measurement, units_bought, price_per_unit FROM purchase WHERE purchase_id = ?";
        try {
            Purchase p = jdbc.queryForObject(sql, PURCHASE_ROW_MAPPER, id);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Purchase insert(Purchase purchase) {
        final String sql = "INSERT INTO purchase (supplier_id, date_of_purchase, delivery_date, unit_of_measurement, units_bought, price_per_unit) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // 1. Supplier ID
            if (purchase.getSupplier() != null && purchase.getSupplier().getSupplier_id() != null && purchase.getSupplier().getSupplier_id() > 0) {
                ps.setObject(1, purchase.getSupplier().getSupplier_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            // 2. Date of Purchase
            if (purchase.getDate_of_purchase() != null) {
                ps.setDate(2, Date.valueOf(purchase.getDate_of_purchase()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            // 3. Delivery Date
            if (purchase.getDelivery_date() != null) {
                ps.setDate(3, Date.valueOf(purchase.getDelivery_date()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            // 4. Unit of Measurement
            ps.setString(4, purchase.getUnit_of_measurement());

            // 5. Units Bought
            if (purchase.getUnits_bought() != null) {
                ps.setFloat(5, purchase.getUnits_bought());
            } else {
                ps.setNull(5, Types.FLOAT);
            }

            // 6. Price Per Unit
            if (purchase.getPrice_per_unit() != null) {
                ps.setFloat(6, purchase.getPrice_per_unit());
            } else {
                ps.setNull(6, Types.FLOAT);
            }

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            purchase.setPurchase_id(key.intValue());
        }
        return purchase;
    }

    public Purchase save(Purchase purchase) {
        if (purchase.getPurchase_id() != null && purchase.getPurchase_id() > 0) {
            String sql = "UPDATE purchase SET supplier_id = ?, date_of_purchase = ?, delivery_date = ?, unit_of_measurement = ?, units_bought = ?, price_per_unit = ? WHERE purchase_id = ?";

            Object supplierId = (purchase.getSupplier() != null && purchase.getSupplier().getSupplier_id() != null && purchase.getSupplier().getSupplier_id() > 0)
                    ? purchase.getSupplier().getSupplier_id() : null;

            Date dateOfPurchase = purchase.getDate_of_purchase() != null ? Date.valueOf(purchase.getDate_of_purchase()) : null;
            Date deliveryDate = purchase.getDelivery_date() != null ? Date.valueOf(purchase.getDelivery_date()) : null;

            jdbc.update(sql, supplierId, dateOfPurchase, deliveryDate, purchase.getUnit_of_measurement(), purchase.getUnits_bought(), purchase.getPrice_per_unit(), purchase.getPurchase_id());
            return purchase;
        } else {
            return insert(purchase);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM purchase WHERE purchase_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM purchase WHERE purchase_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}