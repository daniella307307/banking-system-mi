package com.daniella.bms.utils.helpers;

import com.daniella.bms.enums.EGender;
import com.daniella.bms.models.User;
import com.daniella.bms.services.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class Helper {
    private static IUserService userService;
    @Autowired
    public Helper(
            IUserService userService
    ){
        Helper.userService=userService;
    }
    public User getLoggedInUser(HttpServletRequest request){
        return null;
    }
    public static String getActor(){
        return "Done at: "+ LocalDateTime.now()+"By email: "+userService.getLoggedInUser().getEmail();
    }
    public static void logAction(String message){
        log.info(message);
        log.info(getActor());
    }
    public static EGender getGender(String gender){
        System.out.println("Gender:"+gender);
        return switch (gender.toUpperCase()) {
            case "MALE", "M" -> EGender.MALE;
            case "FEMALE", "F" -> EGender.FEMALE;
            case "OTHER" -> EGender.OTHER;
            default -> throw new RuntimeException("The provided gender is invalid");
        };

    }

}
