package com.daniella.bms.dtos.request.transaction;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DepositOrWithdraw {
    @Positive(message = "Amount must be greater than zero")
    private double amount;
    private String accountNumber;
}
