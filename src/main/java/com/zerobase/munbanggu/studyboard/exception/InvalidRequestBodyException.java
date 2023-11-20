package com.zerobase.munbanggu.studyboard.exception;

import com.zerobase.munbanggu.type.ErrorCode;
import java.util.Map;
import lombok.Getter;

@Getter
public class InvalidRequestBodyException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> errMap;

    public InvalidRequestBodyException(ErrorCode errorCode, Map<String, String> errMap) {
        this.errorCode = errorCode;
        this.errMap = errMap;
    }

}
