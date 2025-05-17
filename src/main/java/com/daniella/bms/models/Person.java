package com.daniella.bms.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private LocalDate dob;
    private Person(String firstname,String lastname,String phoneNumber){
        this.firstname=firstname;
        this.lastname=lastname;
        this.phoneNumber=phoneNumber;
    }

}
