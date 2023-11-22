package com.zerobase.munbanggu.studyboard.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

@Getter
public class NoPermissionException extends RuntimeException {

    private final ErrorCode errorCode;

    public NoPermissionException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
