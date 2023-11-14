package com.zerobase.munbanggu.user.exception;

import com.zerobase.munbanggu.type.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundUserException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundUserException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
