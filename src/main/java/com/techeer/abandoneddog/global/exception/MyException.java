package com.techeer.abandoneddog.global.exception;

import lombok.Getter;

@Getter
public class MyException extends RuntimeException {

	private final ErrorCode errorCode;

	public MyException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
