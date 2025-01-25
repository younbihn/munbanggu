package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import java.util.Map;
import lombok.Getter;

// Request Body 입력 값 중 유효하지 않은 값이 있는 경우 발생하는 예외
@Getter
public class InvalidRequestBodyException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> errMap;

    public InvalidRequestBodyException(ErrorCode errorCode, Map<String, String> errMap) {
        this.errorCode = errorCode;
        this.errMap = errMap;
    }

}
