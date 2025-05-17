package com.daniella.bms.services.impl;

import com.daniella.bms.dtos.request.AccountResponseDTO;
import com.daniella.bms.dtos.request.transaction.CreateTransactionDTO;
import com.daniella.bms.dtos.request.transaction.DepositOrWithdraw;
import com.daniella.bms.dtos.response.TransactionResponseDTO;
import com.daniella.bms.enums.ETransactionType;
import com.daniella.bms.models.Account;
import com.daniella.bms.models.Transaction;
import com.daniella.bms.repositories.IAccountRepository;
import com.daniella.bms.repositories.ITransactionRepository;
import com.daniella.bms.services.services.ITransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final IAccountRepository accountRepository;
    private final ITransactionRepository transactionRepository;
    private Account destination;

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    public static class InvalidTransactionException extends RuntimeException {
        public InvalidTransactionException(String message) {
            super(message);
        }
    }

    @Transactional
    @Override
    public Transaction transfer(@Valid CreateTransactionDTO dto) {
        logger.info("Initiating transfer: sourceAccountNumber={}, destinationAccountNumber={}, amount={}",
                dto.getSourceAccountNumber(), dto.getDestinationAccountNumber(), dto.getAmount());

        if (dto.getAmount() <= 0) {
            logger.warn("Invalid transfer amount: {}", dto.getAmount());
            throw new InvalidTransactionException("Transfer amount must be positive");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(dto.getSourceAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Source account with number " + dto.getSourceAccountNumber() + " not found"));
        Account destinationAccount = accountRepository.findByAccountNumber(dto.getDestinationAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Destination account with number " + dto.getDestinationAccountNumber() + " not found"));

        if (sourceAccount.getBalance() < dto.getAmount()) {
            logger.warn("Insufficient funds in source account: balance={}, requested={}", sourceAccount.getBalance(), dto.getAmount());
            throw new InsufficientFundsException("Insufficient funds in source account");
        }

        // Update balances
        sourceAccount.setBalance(sourceAccount.getBalance() - dto.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + dto.getAmount());

        // Create transaction
        Transaction transaction = Transaction.builder()
                .account(sourceAccount)
                .destination(destinationAccount)
                .amount(dto.getAmount())
                .transactionType(ETransactionType.TRANSFER)
                .build();

        // Save transaction (accounts are updated automatically due to @Transactional)
        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Transfer completed: transactionId={}", savedTransaction.getId());
        return new TransactionResponseDTO(
                savedTransaction.getId(),
                savedTransaction.getAccount().getId(),
                savedTransaction.getDestination() != null ? savedTransaction.getDestination().getId() : null,
                savedTransaction.getAmount(),
                savedTransaction.getTransactionType()
        );
    }

    @Transactional
    @Override
    public TransactionResponseDTO deposit(@Valid DepositOrWithdraw dto) {
        logger.info("Initiating deposit: accountNumber={}, amount={}", dto.getAccountNumber(), dto.getAmount());

        if (dto.getAmount() <= 0) {
            logger.warn("Invalid deposit amount: {}", dto.getAmount());
            throw new InvalidTransactionException("Deposit amount must be positive");
        }

        Account account = accountRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + dto.getAccountNumber() + " not found"));

        account.setBalance(account.getBalance() + dto.getAmount());

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(dto.getAmount())
                .transactionType(ETransactionType.DEPOSIT)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Deposit completed: transactionId={}", savedTransaction.getId());
        return new TransactionResponseDTO(
                savedTransaction.getId(),
                savedTransaction.getAccount().getId(),
                null,
                savedTransaction.getAmount(),
                savedTransaction.getTransactionType()
        );
    }

    @Transactional
    @Override
    public TransactionResponseDTO withdraw(@Valid DepositOrWithdraw dto) {
        logger.info("Initiating withdrawal: accountNumber={}, amount={}", dto.getAccountNumber(), dto.getAmount());

        if (dto.getAmount() <= 0) {
            logger.warn("Invalid withdrawal amount: {}", dto.getAmount());
            throw new InvalidTransactionException("Withdrawal amount must be positive");
        }

        Account account = accountRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + dto.getAccountNumber() + " not found"));

        if (account.getBalance() < dto.getAmount()) {
            logger.warn("Insufficient funds: balance={}, requested={}", account.getBalance(), dto.getAmount());
            throw new InsufficientFundsException("Insufficient funds in account");
        }

        account.setBalance(account.getBalance() - dto.getAmount());

        Transaction transaction = Transaction.builder()
                .account(account)          // From TransactionalDetail
                .amount(amount)            // From TransactionalDetail
                .transactionType("DEBIT")  // From TransactionalDetail
                .destination(destination)  // From Transaction
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Withdrawal completed: transactionId={}", savedTransaction.getId());
        return new TransactionResponseDTO(
                savedTransaction.getId(),
                savedTransaction.getAccount().getId(),
                null,
                savedTransaction.getAmount(),
                savedTransaction.getTransactionType()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public AccountResponseDTO getMyAccount() {
        org.springframework.security.core.userdetails.UserDetails userPrincipal =
                (org.springframework.security.core.userdetails.UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userPrincipal.getUsername();
        logger.debug("Fetching account for user: {}", username);

        Account account = (Account) accountRepository.findByUserEmail(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user " + username));

        logger.info("Retrieved account for user: {}", username);
        return new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUser().getId()
        );
    }
}