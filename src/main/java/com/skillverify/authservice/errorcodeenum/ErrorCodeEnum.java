package com.skillverify.authservice.errorcodeenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    USER_ALREADY_EXISTS("1000", "User already exists"),
    INVALID_CREDENTIALS("1001", "Invalid credentials"),
    USER_NOT_FOUND("1002", "User not found"),
    TOKEN_EXPIRED("1003","Token is invalid or expired"),
    NULL_TOKEN("1004","Token is NULL");
	
	

    private final String code;
    private final String message;
}
