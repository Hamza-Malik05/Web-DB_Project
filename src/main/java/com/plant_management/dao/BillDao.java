package com.plant_management.dao;

import com.plant_management.dto.BillResponseDTO;
import com.plant_management.model.Bill;
import com.plant_management.model.Transaction;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class BillDao {

    private final JdbcTemplate jdbc;

    public BillDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapper for the standard Bill Model
    private final RowMapper<Bill> BILL_ROW_MAPPER = (rs, rowNum) -> {
        Bill b = new Bill();
        b.setBill_id(rs.getInt("bill_id"));
        b.setIssue_date(rs.getTimestamp("issue_date"));
        b.setDue_date(rs.getTimestamp("due_date"));
        b.setBill_type(rs.getString("bill_type"));

        // Creating dummy Transaction object for foreign key
        int transactionId = rs.getInt("transaction_id");
        if (!rs.wasNull()) {
            Transaction t = new Transaction();
            // Assuming your Transaction class has a setTransaction_id method
            t.setTransaction_id(transactionId);
            b.setTransaction(t);
        } else {
            b.setTransaction(null);
        }

        return b;
    };

    // Mapper specifically for your Custom DTO with Timestamp -> LocalDate parsing
    private final RowMapper<BillResponseDTO> BILL_DTO_ROW_MAPPER = (rs, rowNum) -> {

        // 1. Grab the timestamps from the ResultSet
        Timestamp issueTs = rs.getTimestamp("issue_date");
        Timestamp dueTs = rs.getTimestamp("due_date");

        // 2. Parse them into LocalDates (handling potential nulls)
        LocalDate issueLocalDate = (issueTs != null) ? issueTs.toLocalDateTime().toLocalDate() : null;
        LocalDate dueLocalDate = (dueTs != null) ? dueTs.toLocalDateTime().toLocalDate() : null;

        // 3. Return the populated DTO
        return new BillResponseDTO(
                rs.getInt("bill_id"),
                rs.getBigDecimal("amount"),
                issueLocalDate,
                dueLocalDate,
                rs.getString("bill_type"),
                rs.getString("payment_method")
        );
    };

    public List<Bill> findAll() {
        String sql = "SELECT bill_id, issue_date, due_date, bill_type, transaction_id FROM bill";
        return jdbc.query(sql, BILL_ROW_MAPPER);
    }

    public Optional<Bill> findById(Integer id) {
        String sql = "SELECT bill_id, issue_date, due_date, bill_type, transaction_id FROM bill WHERE bill_id = ?";
        try {
            Bill b = jdbc.queryForObject(sql, BILL_ROW_MAPPER, id);
            return Optional.ofNullable(b);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Bill insert(Bill bill) {
        final String sql = "INSERT INTO bill (issue_date, due_date, bill_type, transaction_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (bill.getIssue_date() != null) {
                ps.setTimestamp(1, new Timestamp(bill.getIssue_date().getTime()));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }

            if (bill.getDue_date() != null) {
                ps.setTimestamp(2, new Timestamp(bill.getDue_date().getTime()));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }

            ps.setString(3, bill.getBill_type());

            if (bill.getTransaction() != null && bill.getTransaction().getTransaction_id() != null && bill.getTransaction().getTransaction_id() > 0) {
                ps.setObject(4, bill.getTransaction().getTransaction_id(), Types.INTEGER);
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            bill.setBill_id(key.intValue());
        }
        return bill;
    }

    public Bill save(Bill bill) {
        if (bill.getBill_id() != null && bill.getBill_id() > 0) {
            String sql = "UPDATE bill SET issue_date = ?, due_date = ?, bill_type = ?, transaction_id = ? WHERE bill_id = ?";

            Timestamp issueDate = bill.getIssue_date() != null ? new Timestamp(bill.getIssue_date().getTime()) : null;
            Timestamp dueDate = bill.getDue_date() != null ? new Timestamp(bill.getDue_date().getTime()) : null;

            Object transactionId = (bill.getTransaction() != null && bill.getTransaction().getTransaction_id() != null && bill.getTransaction().getTransaction_id() > 0)
                    ? bill.getTransaction().getTransaction_id() : null;

            jdbc.update(sql, issueDate, dueDate, bill.getBill_type(), transactionId, bill.getBill_id());
            return bill;
        } else {
            return insert(bill);
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM bill WHERE bill_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM bill WHERE bill_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ==========================================
    // CUSTOM METHODS (Calling PostgreSQL Functions)
    // ==========================================

    public List<BillResponseDTO> findAllBillDetails() {
        // Calls the database function we created
        String sql = "SELECT * FROM get_all_bill_details()";
        return jdbc.query(sql, BILL_DTO_ROW_MAPPER);
    }
}