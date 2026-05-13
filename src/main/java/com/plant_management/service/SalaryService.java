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
        log.info("Creating salary record via DB procedure for Employee ID: {}", employeeId);

        // 1. Validation Logic
        employeeDao.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        accountantService.getAccountantById(accountantId)
                .orElseThrow(() -> new RuntimeException("Accountant not found"));

        Optional<Salaries> existingSalary = salariesDao.findByEmployeeIdAndDate(employeeId, date);
        if (existingSalary.isPresent()) {
            throw new RuntimeException("Salary already exists for this date");
        }

        // 2. Call the Database Procedure
        // This handles the Transaction insertion and Salary linking atomically
        salariesDao.createSalaryWithTransaction(
                employeeId,
                accountantId,
                baseAmount,
                bonus,
                fine,
                date,
                paymentMethod
        );

        // 3. Fetch and return the newly created record using our View-based finder
        return salariesDao.findByEmployeeIdAndDate(employeeId, date)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created salary record"));
    }

    @Transactional
    public Salaries updateSalary(Integer salaryId, BigDecimal bonus, BigDecimal fine) {
        log.info("Updating salary ID: {}. New Bonus: {}, New Fine: {}", salaryId, bonus, fine);

        // 1. Find the salary (fetched via view, so it contains transaction data)
        Salaries salary = salariesDao.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("Salary record not found"));

        Transaction transaction = salary.getTransaction();
        if (transaction == null) {
            throw new RuntimeException("Integrity Error: No transaction associated with salary ID " + salaryId);
        }

        // 2. Recalculate Transaction Amount
        // Formula: New Total = (Old Total - Old Bonus + Old Fine) + New Bonus - New Fine
        BigDecimal originalBase = transaction.getAmount()
                .subtract(salary.getBonus())
                .add(salary.getFine());

        BigDecimal newTotalAmount = originalBase.add(bonus).subtract(fine);

        // 3. Update Transaction Table
        transaction.setAmount(newTotalAmount);
        transactionDao.save(transaction);

        // 4. Update Salaries Table
        salariesDao.updateSalary(salaryId, bonus, fine);

        // 5. Return the refreshed record
        return salariesDao.findById(salaryId).get();
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