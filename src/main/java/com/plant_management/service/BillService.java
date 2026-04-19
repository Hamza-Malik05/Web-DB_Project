package com.plant_management.service;

import com.plant_management.dto.BillRequestDTO;
import com.plant_management.dto.BillResponseDTO;
import com.plant_management.model.Bill;
import com.plant_management.dao.AccountantDao;
import com.plant_management.dao.BillDao;
import com.plant_management.dao.TransactionDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillDao billDao;
    private final TransactionDao transactionDao;
    private final AccountantDao accountantDao;
    private final JdbcTemplate jdbcTemplate;

    // Injecting the new DAOs and JdbcTemplate instead of Repositories and EntityManager
    public BillService(BillDao billDao,
                       TransactionDao transactionDao,
                       AccountantDao accountantDao,
                       JdbcTemplate jdbcTemplate) {
        this.billDao = billDao;
        this.transactionDao = transactionDao;
        this.accountantDao = accountantDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get all bills with custom response
    public List<BillResponseDTO> getAllBills() {
        return billDao.findAllBillDetails();
    }

    // Create Bill (and corresponding Transaction)
    @Transactional
    public void createBillViaProcedure(BillRequestDTO dto) {
        // Replaced EntityManager.createNativeQuery with JdbcTemplate.update
        String sql = "CALL create_new_bill(?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                dto.getAmount(),
                dto.getAccountant_id(),
                dto.getPayment_method(),
                dto.getBill_type(),
                dto.getIssue_date(),
                dto.getDue_date()
        );
    }

    // Get Bill by ID
    public Optional<Bill> getBillById(Integer id) {
        return billDao.findById(id);
    }

    // Save bill (direct)
    public Bill saveBill(Bill bill) {
        return billDao.save(bill);
    }

    // Delete bill
    public void deleteBill(Integer id) {
        billDao.deleteById(id);
    }
}