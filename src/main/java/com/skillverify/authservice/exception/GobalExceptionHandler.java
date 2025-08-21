package com.skillverify.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.skillverify.authservice.dto.ErrorResponse;
import com.skillverify.authservice.errorcodeenum.ErrorCodeEnum;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    	
    	ErrorResponse response = new ErrorResponse(
    			ErrorCodeEnum.USER_NOT_FOUND.getCode(),
    			ErrorCodeEnum.USER_NOT_FOUND.getMessage());
    	
    	
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
       
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
       ErrorResponse response = new ErrorResponse(
    		   ErrorCodeEnum.USER_ALREADY_EXISTS.getCode(),
    		   ErrorCodeEnum.USER_ALREADY_EXISTS.getMessage());
       
       return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex) {
    	
    	ErrorResponse response = ErrorResponse.builder()
    			.errorCode(ErrorCodeEnum.INVALID_CREDENTIALS.getCode())
    			.errorMessage(ErrorCodeEnum.INVALID_CREDENTIALS.getMessage())
    			.build();
    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    public ResponseEntity<ErrorResponse> handleAuthenticationFailure(AuthenticationFailureException ex) {
		ErrorResponse response = new ErrorResponse(
				ErrorCodeEnum.AUTHENTICATION_FAILURE.getCode(),
				ErrorCodeEnum.AUTHENTICATION_FAILURE.getMessage());
		
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

    
    @ExceptionHandler(TokenExpireException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpireException(TokenExpireException ex){
    	ErrorResponse response = new ErrorResponse(
    			ErrorCodeEnum.TOKEN_EXPIRED.getCode(),
    			ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
    	
    	
    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                Map.of(
                        "errorCode", "9999",
                        "message", "Something went wrong",
                        "details", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    
    @ExceptionHandler(TokenNullException.class)
    public ResponseEntity<ErrorResponse> handleTokenNullException(TokenNullException ex){
    	
    	ErrorResponse response = new ErrorResponse(
    			ErrorCodeEnum.NULL_TOKEN.getCode(),
    			ErrorCodeEnum.NULL_TOKEN.getMessage());
    	
    	
    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
}