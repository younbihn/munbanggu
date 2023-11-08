package com.zerobase.munbanggu.user.controller;


import com.zerobase.munbanggu.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.zerobase.munbanggu.user.dto.SignInDto;
import com.zerobase.munbanggu.user.service.UserService;
import com.zerobase.munbanggu.util.JwtService;
import com.zerobase.munbanggu.user.service.SendMailService;
import com.zerobase.munbanggu.user.service.SendMessageService;
import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import java.util.Map;
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
    public ResponseEntity<String> signIn(@RequestBody SignInDto signInDto){
        System.out.println(signInDto);
        return ResponseEntity.ok(userService.signIn(signInDto));
    }


    @PostMapping("/sign-out")
    public ResponseEntity<String> logOut( @RequestHeader(name = AUTH_HEADER) String token){

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
  
    @PostMapping("/email-send") //이메일 발송
    public ResponseEntity<AuthenticationStatus> sendMail(@RequestBody Map<String,String> req){
        return ResponseEntity.ok(sendMailService.sendMailVerification(req.get("email")));
    }

    @PostMapping("/email-auth") //이메일 인증
    public ResponseEntity<AuthenticationStatus> verifyMail(@RequestBody Map<String,String> req){
        return ResponseEntity.ok(sendMailService.verifyCode(req.get("email"),req.get("code")));
    }

    @PostMapping("/phone-send") // 핸드폰 인증번호 발송
    public ResponseEntity<AuthenticationStatus> sendSMS(@RequestBody Map<String,String> req){
        return ResponseEntity.ok(sendMessageService.sendMessage(req.get("phoneNumber")));
    }

    @PostMapping("/phone-auth") // 핸드폰 인증
    public ResponseEntity<AuthenticationStatus> verifySMS(@RequestBody Map<String,String> req){
        return ResponseEntity.ok(sendMessageService.verifyCode(req.get("phoneNumber"),req.get("code")));

    }
}
