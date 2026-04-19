package com.plant_management.model;
import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionId implements java.io.Serializable {
    private Integer transactionNumber;
    private Integer accountant_id;
}
