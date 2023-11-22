package com.zerobase.munbanggu.studyboard.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundPostException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundPostException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
