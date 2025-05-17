package com.daniella.bms.services.services;

import com.daniella.bms.dtos.request.AccountResponseDTO;
import com.daniella.bms.dtos.request.transaction.CreateTransactionDTO;
import com.daniella.bms.dtos.request.transaction.DepositOrWithdraw;
import com.daniella.bms.models.Transaction;

public interface ITransactionService {
    public Transaction transfer(CreateTransactionDTO dto);
    public Transaction deposit(DepositOrWithdraw dto);
    public Transaction withdraw(DepositOrWithdraw dto);
    AccountResponseDTO getMyAccount();
}
