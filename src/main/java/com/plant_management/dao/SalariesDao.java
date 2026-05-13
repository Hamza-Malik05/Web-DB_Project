package com.plant_management.dao;

import com.plant_management.model.Employee;
import com.plant_management.model.Salaries;
import com.plant_management.model.Transaction;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class SalariesDao {

    private final JdbcTemplate jdbc;

    public SalariesDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Maps rows from the 'view_salary_details' which includes
     * joined data from Employees and Transactions.
     */
    private final RowMapper<Salaries> FULL_SALARIES_MAPPER = (rs, rowNum) -> {
        Salaries s = new Salaries();
        s.setSalaryId(rs.getInt("salary_id"));
        s.setBonus(rs.getBigDecimal("bonus"));
        s.setFine(rs.getBigDecimal("fine"));

        // Populate Employee Object
        Employee e = new Employee();
        e.setEmployee_id(rs.getInt("employee_id"));
        e.setFirst_name(rs.getString("first_name"));
        e.setLast_name(rs.getString("last_name"));
        e.setDesignation(rs.getString("designation"));
        s.setEmployee(e);

        // Populate Transaction Object
        int txId = rs.getInt("transaction_id");
        if (!rs.wasNull()) {
            Transaction t = new Transaction();
            t.setTransaction_id(txId);
            t.setAmount(rs.getBigDecimal("amount"));
            t.setPayment_method(rs.getString("payment_method"));
            t.setDate_of_transaction(rs.getDate("date_of_transaction"));
            s.setTransaction(t);
        } else {
            s.setTransaction(null);
        }
        return s;
    };

    /**
     * Creates both a Transaction and a Salary record using the DB Procedure.
     */
    public void createSalaryWithTransaction(Integer employeeId, Integer accountantId, BigDecimal baseAmount,
                                            BigDecimal bonus, BigDecimal fine, Date date, String paymentMethod) {
        // SQL for calling the procedure
        String sql = "CALL create_salary_record(?, ?, ?, ?, ?, ?, ?::payment_method_type)";

        jdbc.execute(sql, (PreparedStatement ps) -> {
            ps.setInt(1, employeeId);
            ps.setInt(2, accountantId);
            ps.setBigDecimal(3, baseAmount);
            ps.setBigDecimal(4, bonus);
            ps.setBigDecimal(5, fine);
            ps.setDate(6, new java.sql.Date(date.getTime()));
            ps.setString(7, paymentMethod);
            return ps.execute();
        });
    }

    public List<Salaries> findAll() {
        // Selecting from the View to satisfy the Full Mapper
        String sql = "SELECT * FROM view_salary_details";
        return jdbc.query(sql, FULL_SALARIES_MAPPER);
    }

    public Optional<Salaries> findById(Integer id) {
        String sql = "SELECT * FROM view_salary_details WHERE salary_id = ?";
        try {
            Salaries s = jdbc.queryForObject(sql, FULL_SALARIES_MAPPER, id);
            return Optional.ofNullable(s);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void updateSalary(Integer salaryId, BigDecimal bonus, BigDecimal fine) {
        String sql = "UPDATE salaries SET bonus = ?, fine = ? WHERE salary_id = ?";
        jdbc.update(sql, bonus, fine, salaryId);
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM salaries WHERE salary_id = ?";
        jdbc.update(sql, id);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM salaries WHERE salary_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // ==========================================
    // CUSTOM METHODS (Fetching via View/Functions)
    // ==========================================

    public Optional<Salaries> findByEmployeeIdAndDate(Integer employeeId, Date date) {
        // Note: Ensure the function get_salary_by_employee_and_date returns columns
        // that match view_salary_details or select from the view directly:
        String sql = "SELECT * FROM view_salary_details WHERE employee_id = ? AND date_of_transaction = ?";
        try {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            Salaries s = jdbc.queryForObject(sql, FULL_SALARIES_MAPPER, employeeId, sqlDate);
            return Optional.ofNullable(s);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Salaries> findAllByDate(Date date) {
        String sql = "SELECT * FROM view_salary_details WHERE date_of_transaction = ?";
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return jdbc.query(sql, FULL_SALARIES_MAPPER, sqlDate);
    }

    public List<Salaries> findAllByEmployeeId(Integer employeeId) {
        String sql = "SELECT * FROM view_salary_details WHERE employee_id = ?";
        return jdbc.query(sql, FULL_SALARIES_MAPPER, employeeId);
    }
}