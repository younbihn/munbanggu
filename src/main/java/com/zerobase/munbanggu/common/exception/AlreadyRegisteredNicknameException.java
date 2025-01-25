package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyRegisteredNicknameException extends RuntimeException {

    private final ErrorCode errorCode;

    public AlreadyRegisteredNicknameException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
