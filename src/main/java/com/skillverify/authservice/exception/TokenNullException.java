package com.skillverify.authservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TokenNullException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public TokenNullException(String message) {
		super(message);
	}

}
