package com.daniella.bms.models;

import com.daniella.bms.common.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Account extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;
    private double balance =0.0;
    @Column(unique = true,nullable = false)
    private String accountNumber;
    @OneToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private Customer owner;
    public Account(Customer owner){
        this.owner = owner;
    }
}
