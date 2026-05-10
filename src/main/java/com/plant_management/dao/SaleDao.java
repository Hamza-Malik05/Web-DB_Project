package com.plant_management.dao;

import com.plant_management.model.Sale;
import com.plant_management.model.Transaction;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SaleDao {

    private final JdbcTemplate jdbc;

    public SaleDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Sale> SALE_ROW_MAPPER = (rs, rowNum) -> {
        Sale s = new Sale();
        s.setSale_id(rs.getInt("sale_id"));
        s.setOrder_id(rs.getInt("order_id"));
        s.setTransaction_id(rs.getInt("transaction_id"));
        s.setUnits_sold(rs.getFloat("units_sold"));
        s.setStatus(rs.getString("sale_status"));

        // Create and populate the nested Transaction object
        Transaction t = new Transaction();
        t.setTransaction_id(rs.getInt("transaction_id"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setDate_of_transaction(rs.getDate("date_of_transaction"));
        t.setPayment_method(rs.getString("payment_method"));

        // If your Transaction entity doesn't have 'accountant_name',
        // you can either add it there or handle it as needed.
        // t.setAccountantName(rs.getString("accountant_name"));

        s.setTransaction(t); // Attach the transaction to the sale
        return s;
    };

    public List<Sale> findAll() {
        // Querying the View instead of the Table
        String sql = "SELECT * FROM view_sales_details";
        return jdbc.query(sql, SALE_ROW_MAPPER);
    }

    public Optional<Sale> findById(Integer id) {
        // Querying the View instead of the Table
        String sql = "SELECT * FROM view_sales_details WHERE sale_id = ?";
        try {
            Sale s = jdbc.queryForObject(sql, SALE_ROW_MAPPER, id);
            return Optional.ofNullable(s);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Sale insert(Sale sale) {
        // Logic remains on the base table
        final String sql = "INSERT INTO sales (transaction_id, order_id, units_sold, status) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, sale.getTransaction_id());
            ps.setInt(2, sale.getOrder_id());
            ps.setFloat(3, sale.getUnits_sold());
            ps.setString(4, sale.getStatus());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            sale.setSale_id(key.intValue());
        }
        return sale;
    }

    public Sale save(Sale sale) {
        if (sale.getSale_id() > 0) {
            // Logic remains on the base table
            String sql = "UPDATE sales SET transaction_id = ?, order_id = ?, units_sold = ?, status = ? WHERE sale_id = ?";
            jdbc.update(sql, sale.getTransaction_id(), sale.getOrder_id(), sale.getUnits_sold(), sale.getStatus(), sale.getSale_id());
            return sale;
        } else {
            return insert(sale);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM sales WHERE sale_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM sales WHERE sale_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}