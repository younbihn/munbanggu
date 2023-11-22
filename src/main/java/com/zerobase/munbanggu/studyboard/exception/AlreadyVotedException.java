package com.zerobase.munbanggu.studyboard.exception;

import com.zerobase.munbanggu.common.type.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyVotedException extends RuntimeException {

    private final ErrorCode errorCode;

    public AlreadyVotedException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
