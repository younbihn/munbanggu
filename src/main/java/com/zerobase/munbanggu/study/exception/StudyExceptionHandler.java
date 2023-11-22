package com.zerobase.munbanggu.study.exception;

import com.zerobase.munbanggu.common.dto.ErrorResponse;
import com.zerobase.munbanggu.user.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StudyExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundStudyExceptionHandler(NotFoundStudyException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> invalidTokenExceptionHandler(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(StudyException.class)
    public ResponseEntity<ErrorResponse> studyNotFoundException(StudyException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }
}
