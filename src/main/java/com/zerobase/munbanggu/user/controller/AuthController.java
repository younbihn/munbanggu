package com.zerobase.munbanggu.user.controller;


import com.zerobase.munbanggu.dto.TokenResponse;
import com.zerobase.munbanggu.user.dto.FindUserInfoDto;
import com.zerobase.munbanggu.user.dto.MailVerificationDto;
import com.zerobase.munbanggu.user.dto.ResetPwDto;
import com.zerobase.munbanggu.user.dto.SmsVerificationInfo;
import com.zerobase.munbanggu.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.zerobase.munbanggu.user.dto.SignInDto;
import com.zerobase.munbanggu.user.service.UserService;
import com.zerobase.munbanggu.util.JwtService;
import com.zerobase.munbanggu.user.service.SendMailService;
import com.zerobase.munbanggu.user.service.SendMessageService;
import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @param mailVerificationDto - email, uuidToken
     * @return AuthenticationStatus.SUCCESS / AuthenticationStatus.FAIL (인증성공여부)
     */
    @GetMapping(value = "/verify-email") //이메일 인증
    public ResponseEntity<AuthenticationStatus> verifyMail(MailVerificationDto mailVerificationDto) {
        return ResponseEntity.ok(sendMailService.verifyEmail(mailVerificationDto));
    }

    @PostMapping("/verify-phone") // 핸드폰 인증
    public ResponseEntity<AuthenticationStatus> verifySMS(@RequestBody SmsVerificationInfo smsVerificationInfo) {
        return ResponseEntity.ok(sendMessageService.verifyCode(smsVerificationInfo));
    }

    @PostMapping("/find-id/{user_id}")
    public ResponseEntity<String> requestFindId(@RequestBody FindUserInfoDto findUserInfoDto) {
        return ResponseEntity.ok(userService.requestFindId(findUserInfoDto));
    }
    @GetMapping("/find-id/{user_id}/confirm")
    public ResponseEntity<String> verifyFindId(@RequestBody FindUserInfoDto findUserInfoDto) {
        return ResponseEntity.ok(userService.returnId(findUserInfoDto));
    }

    @PostMapping("/find-pw/{user_id}")
    public ResponseEntity<String> requestResetPw(@RequestBody FindUserInfoDto findUserInfoDto) {
        return ResponseEntity.ok(userService.requestResetPw(findUserInfoDto));
    }

    @PostMapping("/find-pw/{user_id}/confirm")
    public ResponseEntity<AuthenticationStatus> verifyResetPw(@RequestBody ResetPwDto resetPwDto) {
        return ResponseEntity.ok(userService.verifyResetPw(resetPwDto));
    }
}
