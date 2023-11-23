package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<ErrorResponse> notFoundPostExceptionHandler(NotFoundPostException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<ErrorResponse> alreadyVotedExceptionHandler(AlreadyVotedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ErrorResponse> noPermissionExceptionHandler(NoPermissionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    public ResponseEntity<ErrorResponse> invalidRequestBodyExceptionHandler(InvalidRequestBodyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(e.getErrorCode(), e.getErrMap()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> invalidTokenExceptionHandler(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundStudyException.class)
    public ResponseEntity<ErrorResponse> notFoundStudyExceptionHandler(NotFoundStudyException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ErrorResponse> notFoundUserExceptionHandler(NotFoundUserException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(DuplicatedEmailConflictException.class)
    public ResponseEntity<ErrorResponse> duplicatedExceptionHandler(DuplicatedEmailConflictException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }
}
