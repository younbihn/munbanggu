package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

// 존재하지 않는 스터디 조회하는 경우 발생하는 예외
@Getter
public class NotFoundStudyException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundStudyException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
