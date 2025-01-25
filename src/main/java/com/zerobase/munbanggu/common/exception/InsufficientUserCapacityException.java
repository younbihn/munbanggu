package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

// 이미 투표했는데 다시 투표를 시도하는 경우 발생하는 예외
@Getter
public class InsufficientUserCapacityException extends RuntimeException {
    private final ErrorCode errorCode;
    public InsufficientUserCapacityException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
