package com.daniella.bms.dtos.request.transaction;

import lombok.Data;

@Data
public class CreateTransactionDTO {
    private double amount;
    private String destinationAccountNumber;
    private String sourceAccountNumber;
}
