package com.zerobase.munbanggu.common.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

// 이미 가입된 이메일로 가입 시도하는 경우 발생하는 예외
@Getter
public class DuplicatedEmailConflictException extends AuthenticationException {

    private final ErrorCode errorCode;

    public DuplicatedEmailConflictException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
