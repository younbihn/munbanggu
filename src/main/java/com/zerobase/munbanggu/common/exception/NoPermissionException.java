package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

// 스터디원 아닌 회원이 스터디에 접근하려는 경우 발생하는 예외
@Getter
public class NoPermissionException extends RuntimeException {

    private final ErrorCode errorCode;

    public NoPermissionException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
