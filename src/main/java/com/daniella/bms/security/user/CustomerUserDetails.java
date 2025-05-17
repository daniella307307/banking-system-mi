package com.daniella.bms.security.user;

import com.daniella.bms.models.User;
import com.daniella.bms.repositories.IUserRepositories;
import com.daniella.bms.security.user.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerUserDetails {
    private final IUserRepositories userRepository;
    @Autowired
    public CustomerUserDetails(IUserRepositories userRepository){
        this.userRepository=userRepository;
    }
    @Transactional
    public UserDetails loadByUserId(UUID id){
        User user = this.userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User not found with id: "+id));
        return UserPrincipal.create(user);
    }
    @Transactional
    public UserDetails loadUserByUsername(String s) throws RuntimeException{
        User user = userRepository.findUserByEmail(s).orElseThrow(()->new UsernameNotFoundException("User not found with email of "+s));
        return UserPrincipal.create(user);
    }
}
