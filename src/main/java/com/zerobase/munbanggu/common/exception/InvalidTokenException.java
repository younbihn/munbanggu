package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import io.jsonwebtoken.JwtException;
import lombok.Getter;

// 유효하지 않는 토큰으로 API 요청하는 경우 발생하는 예외
@Getter
public class InvalidTokenException extends JwtException {

    private final ErrorCode errorCode;

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
