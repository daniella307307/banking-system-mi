package com.daniella.bms.utils;

import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.SecureRandom;

public class UserUtils {
    private static final SecureRandom random = new SecureRandom();
    public static UserPrincipal getLoggedInUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            return (UserPrincipal) authentication.getPrincipal();
        }
        return null;
    }
    public  static String generateToken(){
        int token = 100000 +random.nextInt(900000);
        return String.valueOf(token);
    }
}
