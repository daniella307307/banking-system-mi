package com.daniella.bms.models;

import com.daniella.bms.enums.EAccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User extends Person{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column(unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private EAccountStatus status=EAccountStatus.ACTIVE;
    private boolean isVerified = false;
    @JsonIgnore
    private String passwordResetCode;
    @JsonIgnore
    private LocalDateTime passwordResetCodeGeneratedAt;
    @Transient
    private String fullName;
    public User(){}
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns =  @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles;
    public User(String email,String password){
        this.email=email;
        this.password=password;
    }
}
