package com.plant_management.service;

import com.plant_management.dao.TransactionDao;
import com.plant_management.model.Transaction;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<Transaction> getAllTransactions() {
        return transactionDao.findAll();
    }

    public Optional<Transaction> getTransactionById(Integer id) {
        return transactionDao.findById(id);
    }

    public Transaction updateTransaction(Integer id, Transaction updatedTransaction) {
        Optional<Transaction> optionalTransaction = transactionDao.findById(id);
        if (optionalTransaction.isPresent()) {
            updatedTransaction.setTransaction_id(id);
            return transactionDao.save(updatedTransaction);
        } else {
            return null;
        }
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionDao.save(transaction);
    }

    public void deleteTransaction(Integer id) {
        transactionDao.deleteById(id);
    }
}
