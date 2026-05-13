package com.plant_management.model;


import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    private Integer transaction_id;

    private BigDecimal amount;

    private TransactionType type;

    private Date date_of_transaction;

    private Accountant accountant;

    private String payment_method;

    public enum TransactionType {
        withdrawal,
        deposit,
        salary,

    }
}

