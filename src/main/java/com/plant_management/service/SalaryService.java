package com.plant_management.service;

import com.plant_management.model.Employee;
import com.plant_management.model.Salaries;
import com.plant_management.model.Transaction;
import com.plant_management.dao.EmployeeDao;
import com.plant_management.dao.SalariesDao;
import com.plant_management.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SalaryService {

    @Autowired
    private SalariesDao salariesDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private AccountantService accountantService;

    @Transactional
    public Salaries createSalary(Integer employeeId, Date date, BigDecimal baseAmount, BigDecimal bonus, BigDecimal fine, Integer accountantId, String paymentMethod) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Check if salary already exists for this date
        Optional<Salaries> existingSalary = salariesDao.findByEmployeeIdAndDate(employeeId, date);
        if (existingSalary.isPresent()) {
            throw new RuntimeException("Salary already exists for this date");
        }

        // Get accountant
        var accountant = accountantService.getAccountantById(accountantId)
                .orElseThrow(() -> new RuntimeException("Accountant not found"));

        // Create and save transaction record first
        Transaction transaction = new Transaction();
        transaction.setAmount(baseAmount);
        transaction.setType(Transaction.TransactionType.withdrawal);
        transaction.setDate_of_transaction(date);
        transaction.setPayment_method(paymentMethod);
        transaction.setAccountant(accountant);
        transaction = transactionDao.save(transaction);

        // Create salary record with the saved transaction
        Salaries salary = new Salaries();
        salary.setEmployee(employee);
        salary.setTransaction(transaction);
        salary.setBonus(bonus);
        salary.setFine(fine);

        return salariesDao.save(salary);
    }

    @Transactional
    public Salaries updateSalary(Integer salaryId, BigDecimal bonus, BigDecimal fine) {
        log.info("Starting salary update for ID: {}", salaryId);

        try {
            // Find the salary record
            Salaries salary = salariesDao.findById(salaryId)
                    .orElseThrow(() -> new RuntimeException("Salary record not found"));
            log.info("Found salary record: {}", salary);

            // Get the transaction
            Transaction transaction = salary.getTransaction();
            if (transaction == null) {
                throw new RuntimeException("No transaction found for salary ID: " + salaryId);
            }
            log.info("Found transaction: {}", transaction);

            // Get the original base amount (first transaction amount)
            BigDecimal originalBaseAmount = transaction.getAmount().subtract(salary.getBonus()).add(salary.getFine());
            log.info("Original base amount: {}", originalBaseAmount);

            // Calculate new total using original base amount
            BigDecimal newTotalAmount = originalBaseAmount.add(bonus).subtract(fine);
            log.info("Calculated new total amount: {}", newTotalAmount);

            // Update transaction
            transaction.setAmount(newTotalAmount);
            Transaction savedTransaction = transactionDao.save(transaction);
            log.info("Saved transaction with new amount: {}", savedTransaction.getAmount());

            // Update salary
            salary.setBonus(bonus);
            salary.setFine(fine);
            Salaries savedSalary = salariesDao.save(salary);
            log.info("Saved salary with new values - Bonus: {}, Fine: {}", savedSalary.getBonus(), savedSalary.getFine());

            return savedSalary;
        } catch (Exception e) {
            log.error("Error updating salary: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Salaries> getAllSalaries() {
        return salariesDao.findAll();
    }

    public List<Salaries> getSalariesByDate(Date date) {
        return salariesDao.findAllByDate(date);
    }

    public List<Salaries> getSalariesByEmployee(Integer employeeId) {
        return salariesDao.findAllByEmployeeId(employeeId);
    }

    public Salaries getSalaryById(Integer salaryId) {
        return salariesDao.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("Salary record not found"));
    }
}