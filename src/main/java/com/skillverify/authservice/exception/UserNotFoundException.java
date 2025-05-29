package com.skillverify.authservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	

	public UserNotFoundException(String message) {
		super(message);
		
	}

}
