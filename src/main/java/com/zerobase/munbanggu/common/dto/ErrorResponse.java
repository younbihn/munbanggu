package com.zerobase.munbanggu.common.dto;

import com.zerobase.munbanggu.common.type.ErrorCode;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private ErrorCode errorCode;
    private Object message;

    public static ErrorResponse of(ErrorCode errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(errorMessage)
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, Map<String, String> errorMap) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(errorMap)
                .build();
    }
}


