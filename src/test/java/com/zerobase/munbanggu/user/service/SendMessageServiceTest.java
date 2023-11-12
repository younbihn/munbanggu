package com.zerobase.munbanggu.user.service;

import com.zerobase.munbanggu.user.dto.SmsVerificationInfo;
import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SendMessageServiceTest {
    @Value("${secret.phone-number}")
    private String phoneNumber;

    @Autowired
    SendMessageService sendMessageService;
    @Test
    public void sendMsgTest(){
        sendMessageService.sendVerificationMessage(phoneNumber);
    }

    @Test
    public void verifyCodeTest(){
        SmsVerificationInfo smsVerificationInfo = new SmsVerificationInfo();
        smsVerificationInfo.setToken("abc");
        smsVerificationInfo.setVerificationCode("1234");
        assert (sendMessageService.verifyCode(smsVerificationInfo).equals(AuthenticationStatus.SUCCESS));
    }

}