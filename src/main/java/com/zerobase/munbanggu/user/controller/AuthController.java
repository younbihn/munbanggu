package com.zerobase.munbanggu.user.controller;


import com.zerobase.munbanggu.common.dto.TokenResponse;
import com.zerobase.munbanggu.user.dto.FindUserInfoDto;
import com.zerobase.munbanggu.user.dto.MailVerificationDto;
import com.zerobase.munbanggu.user.dto.ResetPwDto;
import com.zerobase.munbanggu.user.dto.SmsVerificationInfo;
import com.zerobase.munbanggu.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.zerobase.munbanggu.user.dto.SignInDto;
import com.zerobase.munbanggu.user.service.UserService;
import com.zerobase.munbanggu.common.util.JwtService;
import com.zerobase.munbanggu.user.service.SendMailService;
import com.zerobase.munbanggu.user.service.SendMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final SendMailService sendMailService;
    private final SendMessageService sendMessageService;

    private static final String AUTH_HEADER = "Authorization";

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInDto signInDto) {

        return ResponseEntity.ok(userService.signIn(signInDto));
    }


    @PostMapping("/sign-out")
    public ResponseEntity<String> logOut(@RequestHeader(name = AUTH_HEADER) String token) {

        if (jwtService.isBlacklisted(token)) {
            return ResponseEntity.ok("이미 로그아웃된 토큰입니다.");
        }
        jwtService.logout(token);
        return ResponseEntity.ok("로그아웃");
    }

    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(
            @RequestHeader(value = "Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    /**
     * 메일 인증 controller
     *
     * @param mailVerificationDto 메일인증dto
     * @return 성공여부(T/F)
     */
    @GetMapping(value = "/verify-email") //이메일 인증
    public ResponseEntity<Boolean> verifyMail(MailVerificationDto mailVerificationDto) {
        return ResponseEntity.ok(sendMailService.verifyEmail(mailVerificationDto));
    }

    /**
     * 핸드폰 인증
     *
     * @param smsVerificationInfo 문자인증dto
     * @return 성공여부(T/F)
     */
    @PostMapping("/verify-phone")
    public ResponseEntity<Boolean> verifySMS(@RequestBody SmsVerificationInfo smsVerificationInfo) {
        return ResponseEntity.ok(sendMessageService.verifyCode(smsVerificationInfo));
    }

    /**
     * 아이디 찾기 신청 (유저 정보 확인 후, 문자발송)
     *
     * @param findUserInfoDto 유저정보찾기dto
     * @return token
     */
    @PostMapping("/find-id")
    public ResponseEntity<String> requestFindId(@RequestBody FindUserInfoDto findUserInfoDto) {
        return ResponseEntity.ok().body(userService.requestFindId(findUserInfoDto));
    }

    /**
     * 아이디 반환
     *
     * @param userId            사용자ID
     * @param findUserInfoDto   유저정보찾기dto
     * @return String id
     */
    @GetMapping("/find-id/{user_id}/confirm")
    public ResponseEntity<String> verifyFindId(
        @PathVariable("user_id") Long userId,
        @RequestBody FindUserInfoDto findUserInfoDto) {
        return ResponseEntity.ok().body(userService.returnId(userId,findUserInfoDto));
    }

    /**
     * 비밀번호 재설정 신청 (유저 정보 확인 후, 문자발송)
     *
     * @param userId            사용자ID
     * @param findUserInfoDto   유저정보찾기dto
     * @return token
     */
    @PostMapping("/find-pw/{user_id}")
    public ResponseEntity<String> requestResetPw(
        @PathVariable("user_id") Long userId,
        @RequestBody FindUserInfoDto findUserInfoDto) {
        return ResponseEntity.ok().body(userService.requestResetPw(userId,findUserInfoDto));
    }

    /**
     * 비밀번호 재설정
     *
     * @param userId     사용자ID
     * @param resetPwDto 비밀번호재설정dto
     * @return 성공여부(T/F)
     */
    @PostMapping("/find-pw/{user_id}/confirm")
    public ResponseEntity<Boolean> verifyResetPw(
        @PathVariable("user_id") Long userId,
        @RequestBody ResetPwDto resetPwDto) {
        return ResponseEntity.ok().body(userService.verifyResetPw(userId,resetPwDto));
    }
}
