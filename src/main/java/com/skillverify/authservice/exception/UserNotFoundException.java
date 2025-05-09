package com.skillverify.authservice.exception;

import com.skillverify.authservice.errorcodeenum.ErrorCodeEnum;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final String errorCode;

	public UserNotFoundException(ErrorCodeEnum errorCodeEnum) {
		super(errorCodeEnum.getMessage());
		this.errorCode = errorCodeEnum.getCode();
	}

}
