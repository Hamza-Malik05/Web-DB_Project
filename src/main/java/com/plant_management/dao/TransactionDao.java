// java
package com.plant_management.dao;

import com.plant_management.model.Transaction;
import com.plant_management.model.Accountant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionDao {

    private final JdbcTemplate jdbc;

    public TransactionDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Transaction> ROW_MAPPER = (rs, rowNum) -> {
        Transaction t = new Transaction();
        t.setTransaction_id(rs.getObject("transaction_id") != null ? rs.getInt("transaction_id") : null);
        t.setAmount(rs.getBigDecimal("amount"));

        String typeStr = rs.getString("type");
        if (typeStr != null) {
            t.setType(Transaction.TransactionType.valueOf(typeStr));
        }

        Date d = rs.getDate("date_of_transaction");
        if (d != null) t.setDate_of_transaction(d);

        int accId = rs.getInt("accountant_id");
        if (!rs.wasNull()) {
            Accountant acc = new Accountant();
            acc.setAccountant_id(accId);
            t.setAccountant(acc);
        } else {
            t.setAccountant(null);
        }

        t.setPayment_method(rs.getString("payment_method"));
        return t;
    };

    public List<Transaction> findAll() {
        String sql = "SELECT transaction_id, amount, type, date_of_transaction, accountant_id, payment_method FROM transactions";
        return jdbc.query(sql, ROW_MAPPER);
    }

    public Optional<Transaction> findById(Integer id) {
        String sql = "SELECT transaction_id, amount, type, date_of_transaction, accountant_id, payment_method FROM transactions WHERE transaction_id = ?";
        try {
            Transaction t = jdbc.queryForObject(sql, ROW_MAPPER, id);
            return Optional.ofNullable(t);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Transaction save(Transaction tx) {
        if (tx.getTransaction_id() == null) {
            // Added :: casts to the insert string
            final String insertSql = "INSERT INTO transactions (amount, type, date_of_transaction, accountant_id, payment_method) " +
                    "VALUES (?, ?, ?, ?, ?::payment_method_type)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                // Note: Standard PreparedStatement doesn't always love the :: syntax inside prepareStatement()
                // If the code below throws an error, use the string without :: but ensure ps.setObject(index, value, Types.OTHER)
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

                if (tx.getAmount() != null) {
                    ps.setBigDecimal(1, tx.getAmount());
                } else {
                    ps.setNull(1, Types.DECIMAL);
                }

                if (tx.getType() != null) {
                    ps.setString(2, tx.getType().name());
                } else {
                    ps.setNull(2, Types.OTHER); // Use OTHER for null Enums
                }

                if (tx.getDate_of_transaction() != null) {
                    ps.setDate(3, new java.sql.Date(tx.getDate_of_transaction().getTime()));
                } else {
                    ps.setNull(3, Types.DATE);
                }

                if (tx.getAccountant() != null) {
                    ps.setObject(4, tx.getAccountant().getAccountant_id(), Types.INTEGER);
                } else {
                    ps.setNull(4, Types.INTEGER);
                }

                if (tx.getPayment_method() != null) {
                    ps.setString(5, tx.getPayment_method());
                } else {
                    ps.setNull(5, Types.OTHER); // Use OTHER for null Enums
                }

                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                tx.setTransaction_id(key.intValue());
            }
            return tx;
        } else {
            // Fixed the Update SQL with explicit casts
            final String updateSql = "UPDATE transactions SET " +
                    "amount = ?, " +
                    "type = ?, " +
                    "date_of_transaction = ?, " +
                    "accountant_id = ?, " +
                    "payment_method = ?::payment_method_type " +
                    "WHERE transaction_id = ?";

            Object accId = (tx.getAccountant() != null) ? tx.getAccountant().getAccountant_id() : null;

            jdbc.update(updateSql,
                    tx.getAmount(),
                    tx.getType() != null ? tx.getType().name() : null,
                    tx.getDate_of_transaction() != null ? new java.sql.Date(tx.getDate_of_transaction().getTime()) : null,
                    accId,
                    tx.getPayment_method(),
                    tx.getTransaction_id());
            return tx;
        }
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        return jdbc.update(sql, id);
    }
}