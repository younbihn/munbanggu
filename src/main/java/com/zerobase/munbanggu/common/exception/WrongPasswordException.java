package com.zerobase.munbanggu.common.exception;

import lombok.Getter;

import com.zerobase.munbanggu.common.type.ErrorCode;

@Getter
public class WrongPasswordException extends RuntimeException {


    private final ErrorCode errorCode;

    public WrongPasswordException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
