package com.zerobase.munbanggu.point.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundPointException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundPointException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
