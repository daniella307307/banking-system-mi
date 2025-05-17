package com.daniella.bms.models;

import com.daniella.bms.common.AbstractEntity;
import com.daniella.bms.enums.ETransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionalDetail extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private double amount;
    @Enumerated(EnumType.STRING)
    private ETransactionType transactionType;
}
