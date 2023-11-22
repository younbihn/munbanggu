package com.zerobase.munbanggu.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // user
    USER_NOT_EXIST("해당 계정은 존재하지 않습니다."),
    USER_WITHDRAWN("해당 계정은 탈퇴된 계정입니다."),
    WRONG_PASSWORD("비밀번호가 일치하지 않습니다."),
    EMAIL_CONFLICT("이미 가입된 이메일 입니다. 다른 방법으로 로그인해 주세요."),
    NOT_FOUND_EMAIL("가입되지 않은 이메일입니다."),
    STUDY_NOT_EXIST("해당 스터디는 존재하지 않습니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    NOT_FOUND_USER_ID("가입되지 않은 회원입니다."),
    INVALID_REQUEST_BODY(""),
    EMAIL_NOT_EXIST("가입되지 않은 이메일입니다."),
    UNAUTHORIZED("로그인 후 접근 가능합니다."),
    ALREADY_REGISTERED_NICKNAME("이미 등록된 닉네임입니다."),
    INVALID_NICKNAME_FORMAT("잘못된 닉네임 형식입니다."),
    USER_UNMATCHED("사용자의 정보가 일치하지 않습니다"),

    // studyboard
    NOT_FOUND_POST("등록되지 않은 게시글 id 입니다."),
    NOT_FOUND_VOTE("등록되지 않은 투표입니다."),
    NOT_FOUND_OPTION("등록되지 않은 투표 항목입니다."),
    ALREADY_VOTED("이미 투표하였습니다."),
    NO_PERMISSION_TO_MODIFY("수정 권한이 없습니다."),
    NOT_FOUND_COMMENT("등록되지 않은 댓글입니다."),

    //verifiation
    INVALID_EMAIL("이메일이 일치하지 않습니다"),
    INVALID_CODE("인증번호가 일치하지 않습니다"),
    INVALID_PHONE("핸드폰번호가 일치하지 않습니다"),

    //study
    NOT_FOUND_STUDY("등록되지 않은 스터디입니다."),
    CHECKLIST_NOT_EXIST("일치하는 체크리스트가  존재하지 않습니다"),
    TOKEN_UNMATCHED("아이디와 토큰정보가 일치하지 않습니다"),
    ALREADY_JOINED("이미 참여한 스터디 입니다."),
    NOT_PARTICIPATING("해당 스터디에 참여하고 있지 않습니다"),
    INVALID_USER_OR_STUDY("사용자 혹은 스터디가 존재하지 않습니다."),
    INSUFFICIENT_USER_CAPACITY("스터디 참여자 수가 최소 정원 이하입니다."),
    NOT_FOUND_STUDY_MEMBER("스터디에 가입되지 않은 회원입니다."),
    ;


    private final String description;
}
