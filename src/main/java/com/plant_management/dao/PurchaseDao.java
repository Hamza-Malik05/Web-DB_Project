// java
package com.plant_management.dao;

import com.plant_management.model.Purchase;
import com.plant_management.model.Supplier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class PurchaseDao {

    private final JdbcTemplate jdbc;
    private final RowMapper<Purchase> PURCHASE_ROW_MAPPER;

    public PurchaseDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;

        // initialize mapper after jdbc is assigned
        this.PURCHASE_ROW_MAPPER = (rs, rowNum) -> {
            Purchase p = new Purchase();
            p.setPurchase_id(rs.getInt("purchase_id"));

            int supplierId = rs.getInt("supplier_id");
            if (!rs.wasNull()) {
                Supplier supplier = new Supplier();
                supplier.setSupplier_id(supplierId);

                // Try to read supplier_name from the resultset first.
                String name = null;
                try {
                    name = rs.getString("supplier_name");
                } catch (Exception ignored) {
                    name = null;
                }

                // If supplier_name is missing/null in the resultset, fetch it directly using supplier_id.
                if (name == null) {
                    try {
                        name = this.jdbc.queryForObject("SELECT name FROM suppliers WHERE supplier_id = ?", String.class, supplierId);
                    } catch (EmptyResultDataAccessException ex) {
                        name = null;
                    }
                }

                if (name != null) {
                    supplier.setName(name);
                }
                p.setSupplier(supplier);
            } else {
                p.setSupplier(null);
            }

            if (rs.getDate("date_of_purchase") != null) {
                p.setDate_of_purchase(rs.getDate("date_of_purchase").toLocalDate());
            }
            if (rs.getDate("delivery_date") != null) {
                p.setDelivery_date(rs.getDate("delivery_date").toLocalDate());
            }

            p.setUnit_of_measurement(rs.getString("unit_of_measurement"));
            p.setUnits_bought(rs.getObject("units_bought", BigDecimal.class));
            p.setPrice_per_unit(rs.getObject("price_per_unit", BigDecimal.class));

            return p;
        };
    }

    public List<Purchase> findAll() {
        String sql = "SELECT p.purchase_id, p.supplier_id, s.name AS supplier_name, p.date_of_purchase, p.delivery_date, p.unit_of_measurement, p.units_bought, p.price_per_unit " +
                "FROM purchases p LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id";
        return jdbc.query(sql, PURCHASE_ROW_MAPPER);
    }

    public Optional<Purchase> findById(Integer id) {
        String sql = "SELECT p.purchase_id, p.supplier_id, s.name AS supplier_name, p.date_of_purchase, p.delivery_date, p.unit_of_measurement, p.units_bought, p.price_per_unit " +
                "FROM purchases p LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id WHERE p.purchase_id = ?";
        try {
            Purchase p = jdbc.queryForObject(sql, PURCHASE_ROW_MAPPER, id);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Purchase insert(Purchase purchase) {
        final String sql = "INSERT INTO purchases (supplier_id, date_of_purchase, delivery_date, unit_of_measurement, units_bought, price_per_unit) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"purchase_id"});

            if (purchase.getSupplier() != null && purchase.getSupplier().getSupplier_id() != null && purchase.getSupplier().getSupplier_id() > 0) {
                ps.setObject(1, purchase.getSupplier().getSupplier_id(), Types.INTEGER);
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            if (purchase.getDate_of_purchase() != null) {
                ps.setDate(2, Date.valueOf(purchase.getDate_of_purchase()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (purchase.getDelivery_date() != null) {
                ps.setDate(3, Date.valueOf(purchase.getDelivery_date()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, purchase.getUnit_of_measurement());

            if (purchase.getUnits_bought() != null) {
                ps.setBigDecimal(5, purchase.getUnits_bought());
            } else {
                ps.setNull(5, Types.NULL);
            }

            if (purchase.getPrice_per_unit() != null) {
                ps.setBigDecimal(6, purchase.getPrice_per_unit());
            } else {
                ps.setNull(6, Types.NULL);
            }

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            purchase.setPurchase_id(key.intValue());
        }
        return purchase;
    }

    public BigDecimal recordNewPurchaseViaFunction(Integer supplierId, BigDecimal unitsBought) {
        String functionCall = "SELECT record_new_purchase(?, ?)";
        return jdbc.queryForObject(functionCall, BigDecimal.class, supplierId, unitsBought);
    }

    public Purchase save(Purchase purchase) {
        if (purchase.getPurchase_id() != null && purchase.getPurchase_id() > 0) {
            String sql = "UPDATE purchases SET supplier_id = ?, date_of_purchase = ?, delivery_date = ?, unit_of_measurement = ?, units_bought = ?, price_per_unit = ? WHERE purchase_id = ?";

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
        String sql = "DELETE FROM purchases WHERE purchase_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM purchases WHERE purchase_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}