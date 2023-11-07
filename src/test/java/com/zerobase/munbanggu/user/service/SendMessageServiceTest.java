package com.zerobase.munbanggu.user.service;

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
        sendMessageService.sendMessage(phoneNumber);
    }

    @Test
    public void verifyCodeTest(){
        String code = "24542";
        assert (sendMessageService.verifyCode(phoneNumber,code).equals(AuthenticationStatus.SUCCESS));
    }

}