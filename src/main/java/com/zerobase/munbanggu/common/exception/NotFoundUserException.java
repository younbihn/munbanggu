package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

// 가입되지 않은 회원 조회하는 경우 발생하는 예외
@Getter
public class NotFoundUserException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundUserException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
