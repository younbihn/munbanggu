package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

// 스터디 정원이 다 찬 경우 발생하는 예외

@Getter
public class FullCapacityException extends RuntimeException {

    private final ErrorCode errorCode;

    public FullCapacityException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
