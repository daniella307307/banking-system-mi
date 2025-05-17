package com.daniella.bms.models;

import com.daniella.bms.models.TransactionalDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "transactions")
public class Transaction extends TransactionalDetail {
    @ManyToOne
    @JoinColumn(name = "destination_id")
    @JsonIgnore
    private Account destination;
}