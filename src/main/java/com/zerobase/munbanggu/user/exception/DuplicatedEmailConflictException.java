package com.zerobase.munbanggu.user.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class DuplicatedEmailConflictException extends AuthenticationException {


    public DuplicatedEmailConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage());

    }

}
