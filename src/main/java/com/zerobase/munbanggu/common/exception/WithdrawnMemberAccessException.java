package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

@Getter
public class WithdrawnMemberAccessException extends RuntimeException {

    private final ErrorCode errorCode;

    public WithdrawnMemberAccessException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
