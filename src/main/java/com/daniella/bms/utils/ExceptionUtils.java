package com.daniella.bms.utils;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;


public class ExceptionUtils {
    private final Logger logger= LoggerFactory.getLogger(ExceptionUtils.class);
    public static void handleResponseException(Exception e) {
        System.out.println(e);
    }
}
