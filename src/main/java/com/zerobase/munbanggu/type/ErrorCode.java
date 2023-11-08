package com.zerobase.munbanggu.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_EXIST("해당 계정은 존재하지 않습니다."),
    USER_WITHDRAWN("해당 계정은 탈퇴된 계정입니다."),
    WRONG_PASSWORD("비밀번호가 일치하지 않습니다."),
    ;
    private String description;
}
