package com.techeer.abandoneddog.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleApiRequestException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
		return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
	}

	@ExceptionHandler(MyException.class)
	public ResponseEntity<Object> handleCustomException(MyException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse errorResponse = new ErrorResponse(errorCode);
		return new ResponseEntity<>(errorResponse, errorCode.getStatus());
	}

}
