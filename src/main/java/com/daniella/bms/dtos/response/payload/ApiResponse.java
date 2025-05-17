package com.daniella.bms.dtos.response.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T> {
    private boolean success;
    private String message;
    private HttpStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T payload;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error = null;
    private final String timestamp = LocalDateTime.now().toString();
    public ApiResponse(boolean success, String message, HttpStatus status, T payload, boolean isPayload){
        this.success = success;
        this.message=message;
        this.status=status;
        this.payload=payload;
        this.error=null;
    }
    public ApiResponse(boolean success, String message, HttpStatus status, Object error){
        this.success=success;
        this.message=message;
        this.status=status;
        this.payload=null;
        this.error=error;
    }
    public static <T> ResponseEntity<ApiResponse<T>> success(String message,HttpStatus status,T payload){
        ApiResponse<T> response= new ApiResponse<>(true,message,status,payload,true);
        return new ResponseEntity<>(response,status);
    }
    public static <T> ResponseEntity<ApiResponse<T>> fail(String message,HttpStatus status,Object error){
        ApiResponse<T> response= new ApiResponse<>(false,message,status,error);
        return new ResponseEntity<>(response,status);
    }
}
