package com.zerobase.munbanggu.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // user
    USER_NOT_EXIST("해당 계정은 존재하지 않습니다."),
    USER_WITHDRAWN("해당 계정은 탈퇴된 계정입니다."),
    WRONG_PASSWORD("비밀번호가 일치하지 않습니다."),
    EMAIL_CONFLICT("이미 가입된 이메일 입니다. 다른 방법으로 로그인해 주세요."),
    NOT_FOUND_EMAIL("가입되지 않은 이메일입니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    NOT_FOUND_USER_ID("가입되지 않은 회원입니다."),
    INVALID_REQUEST_BODY(""),

    // studyboard
    POST_NOT_FOUND("등록되지 않은 게시글 id 입니다."),
    VOTE_NOT_FOUND("등록되지 않은 투표입니다."),
    NOT_FOUND_OPTION("등록되지 않은 투표 항목입니다."),
    ALREADY_VOTED("이미 투표하였습니다.");

    private final String description;
}
