package com.skillverify.authservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TokenExpireException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TokenExpireException(String message) {
        super(message);
    }
}