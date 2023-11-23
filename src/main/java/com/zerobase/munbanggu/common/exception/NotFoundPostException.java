package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

// 존재하지 않는 게시글에 대해 요청하는 경우 발생하는 예외
@Getter
public class NotFoundPostException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundPostException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
