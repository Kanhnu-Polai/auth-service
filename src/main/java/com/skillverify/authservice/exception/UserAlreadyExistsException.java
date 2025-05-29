// Updated: UserAlreadyExistsException.java
package com.skillverify.authservice.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public UserAlreadyExistsException(String message) {
		super(message);
	}
	
	
    
}