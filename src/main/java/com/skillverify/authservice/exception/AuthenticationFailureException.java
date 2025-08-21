package com.skillverify.authservice.exception;

import com.skillverify.authservice.errorcodeenum.ErrorCodeEnum;

public class AuthenticationFailureException  extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private ErrorCodeEnum errorCodeEnum;
	
	public AuthenticationFailureException(ErrorCodeEnum errorCodeEnum) {
		super(errorCodeEnum.getMessage());
		this.errorCodeEnum = errorCodeEnum;
	}

}
