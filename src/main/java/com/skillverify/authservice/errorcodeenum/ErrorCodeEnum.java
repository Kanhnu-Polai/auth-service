package com.skillverify.authservice.errorcodeenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    USER_ALREADY_EXISTS("AUTH_SERVICE_1000", "User already exists"),
    INVALID_CREDENTIALS("AUTH_SERVICE_1001", "Invalid credentials"),
    USER_NOT_FOUND("AUTH_SERVICE_1002", "User not found"),
    AUTHENTICATION_FAILURE("AUTH_SERVICE_1003", "Authentication failed"),
    TOKEN_EXPIRED("AUTH_SERVICE_1004","Token is invalid or expired"),
    NULL_TOKEN("AUTH_SERVICE_1005","Token is NULL");
	
	

    private final String code;
    private final String message;
}
