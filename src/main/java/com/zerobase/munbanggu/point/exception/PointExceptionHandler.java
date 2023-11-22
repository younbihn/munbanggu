package com.zerobase.munbanggu.point.exception;

import com.zerobase.munbanggu.common.dto.ErrorResponse;
import com.zerobase.munbanggu.study.exception.NotFoundStudyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PointExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundStudyExceptionHandler(NotFoundStudyException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

}
