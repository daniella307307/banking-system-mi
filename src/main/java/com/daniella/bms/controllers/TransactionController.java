package com.daniella.bms.controllers;

import com.daniella.bms.dtos.request.AccountResponseDTO;
import com.daniella.bms.dtos.request.transaction.CreateTransactionDTO;
import com.daniella.bms.dtos.request.transaction.DepositOrWithdraw;
import com.daniella.bms.dtos.response.payload.ApiResponse;
import com.daniella.bms.models.Transaction;
import com.daniella.bms.services.services.ITransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction")
public class TransactionController {
    private final ITransactionService transactionService;



    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Transaction>> deposit(@Valid @RequestBody DepositOrWithdraw dto) {
        try{
            Transaction transaction = transactionService.deposit(dto);
            return ApiResponse.success("Deposit successful", HttpStatus.OK, transaction);
        }catch (Exception e){
            return ApiResponse.fail("Deposit Failed",BAD_REQUEST,e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Transaction>> withdraw(@Valid  @RequestBody DepositOrWithdraw dto) {
        try{
            Transaction transaction = transactionService.withdraw(dto);
            return ApiResponse.success("Withdraw successful", HttpStatus.OK, transaction);
        }catch (Exception e){
            return ApiResponse.fail("Withdraw Failed",BAD_REQUEST,e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Transaction>> transfer(@Valid  @RequestBody CreateTransactionDTO dto) {
        try{
            Transaction transaction = transactionService.transfer(dto);
            return ApiResponse.success("Transfer successful", HttpStatus.OK, transaction);
        }catch (Exception e){
            return ApiResponse.fail("Transfer Failed",BAD_REQUEST,e.getMessage());
        }
    }


    @GetMapping("/my-account")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> getMyAccount() {
        try{
            AccountResponseDTO account = transactionService.getMyAccount();
            return ApiResponse.success("Account successful", HttpStatus.OK, account);
        }catch (Exception e){
            return ApiResponse.fail("Get My Account Failed",BAD_REQUEST,e.getMessage());
        }
    }
}
