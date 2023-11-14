package com.zerobase.munbanggu.user.exception;

import com.zerobase.munbanggu.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> invalidTokenExceptionHandler(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ErrorResponse> notFoundUserExceptionHandler(NotFoundUserException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
}
