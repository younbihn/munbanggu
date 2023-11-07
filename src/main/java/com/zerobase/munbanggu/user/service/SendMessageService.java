package com.zerobase.munbanggu.user.service;

import static com.zerobase.munbanggu.user.type.RedisTime.PHONE_VALID;

import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendMessageService {
    private final RedisUtil redisUtil;
    private final int CODE_LENGTH = 5;

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.secret-key}")
    private String secretKey;

    @Value("${secret.phone-number}")
    private String senderPhoneNumber;

    public AuthenticationStatus sendMessage(String phoneNumber) {
        String key = RandomStringUtils.random(CODE_LENGTH,false,true);
        Message coolsms = new Message(apiKey, secretKey);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);
        params.put("from", senderPhoneNumber);
        params.put("type", "SMS");
        params.put("text", "[문방구] 핸드폰 인증 메세지 \n 인증번호는 ["+key+"] 입니다.");
        params.put("app_version", "test app 1.2");

        log.info("\n"+params.toString());

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            redisUtil.setData(phoneNumber,key,PHONE_VALID.getTime());
            return AuthenticationStatus.SUCCESS;
        } catch (CoolsmsException e) {
            log.info(e.getMessage());
            return AuthenticationStatus.FAIL;
        }
    }

    public AuthenticationStatus verifyCode(String phoneNumber, String input) {
        String code = redisUtil.getData(phoneNumber);

        if (code.equals(input)) {
            return AuthenticationStatus.SUCCESS;
        }
        return AuthenticationStatus.FAIL;
    }
}
