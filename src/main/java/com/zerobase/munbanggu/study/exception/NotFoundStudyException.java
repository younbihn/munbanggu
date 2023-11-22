package com.zerobase.munbanggu.study.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundStudyException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundStudyException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
