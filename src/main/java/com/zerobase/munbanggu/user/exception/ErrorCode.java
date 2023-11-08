package com.zerobase.munbanggu.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_CONFLICT("이미 가입된 이메일 입니다. 다른 방법으로 로그인해 주세요."),
    NOT_FOUND_EMAIL("가입되지 않은 이메일입니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다.");

    private final String message;
}
