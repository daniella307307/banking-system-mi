package com.daniella.bms.models;

import com.daniella.bms.enums.EAccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "users")
public class User extends Person {

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private EAccountStatus status = EAccountStatus.ACTIVE;
    private boolean isVerified = false;

    @JsonIgnore
    private String passwordResetCode;

    @JsonIgnore
    private LocalDateTime passwordResetCodeGeneratedAt;

    private LocalDateTime verificationCodeGeneratedAt;
    private int verificationCode;

    @Transient
    private String fullName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    public String getFullName() {
        return getFirstname() + " " + getLastname();
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String firstname, String lastname, LocalDate dob, String phoneNumber,
                String password, Set<Role> roles) {
        super();  // Ensures superclass constructor is called if needed
        this.email = email;
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setDob(dob);
        this.setPhoneNumber(phoneNumber);
        this.password = password;
        this.roles = roles;
    }

    public void setVerificationCode() {
    }
}
