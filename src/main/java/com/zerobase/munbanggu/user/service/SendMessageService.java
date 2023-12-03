package com.zerobase.munbanggu.user.service;

import static com.zerobase.munbanggu.common.type.RedisTime.PHONE_VALID;

import com.zerobase.munbanggu.common.exception.InvalidTokenException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.common.util.RedisUtil;
import com.zerobase.munbanggu.user.dto.SmsVerificationInfo;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendMessageService {

    private final RedisUtil redisUtil;

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.secret-key}")
    private String secretKey;

    @Value("${secret.phone-number}")
    private String senderPhoneNumber;
    private final int VERIFY_CODE_LEN = 5;
    private final AuthService authService;

    /**
     * 인증 문자 발송
     * @param phoneNumber 핸드폰번호
     * @return 토큰
     */
    public String sendVerificationMessage(String phoneNumber) {
        String verificationCode = RandomStringUtils.random(VERIFY_CODE_LEN, false, true);
        String token = authService.createVerificationToken(phoneNumber, PHONE_VALID.getTime());

        Message coolsms = new Message(apiKey, secretKey);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);
        params.put("from", senderPhoneNumber);
        params.put("type", "SMS");
        params.put("text", "[문방구] 핸드폰 인증 메세지 \n 인증번호는 [" + verificationCode + "] 입니다.");
        params.put("app_version", "test app 1.2");

        try {
            coolsms.send(params);
            redisUtil.setMsgVerificationInfo(token, phoneNumber, verificationCode, PHONE_VALID.getTime());
        } catch (CoolsmsException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return token;
    }

    /**
     * 코드인증
     *
     * @param smsVerificationInfo - token, phoneNumber,inputCode
     * @return AuthenticationStatus(SUCCESS / FAIL)
     */
    public boolean verifyCode(SmsVerificationInfo smsVerificationInfo) {
        SmsVerificationInfo info = redisUtil.getMsgVerificationInfo(smsVerificationInfo.getToken());

        if (info == null) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }
        return info.getVerificationCode().equals(smsVerificationInfo.getVerificationCode());
    }
}
