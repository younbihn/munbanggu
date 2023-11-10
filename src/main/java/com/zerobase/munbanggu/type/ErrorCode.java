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
    EMAIL_NOT_EXISTS("해당 이메일로 가입된 회원정보가 존재하지 않습니다"),
    EMAIL_CONFLICT("이미 가입된 이메일 입니다. 다른 방법으로 로그인해 주세요."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    NOT_FOUND_EMAIL("가입되지 않은 이메일입니다."),




    ;
    private String description;
}
