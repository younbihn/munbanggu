package com.zerobase.munbanggu.user.service;

import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SendMailServiceTest {

    @Value("${secret.recipient}")
    private String recipient_email ;

    @Autowired
    private SendMailService sendMailService;

    @Test
    public void sendMailTest(){
        try{
            sendMailService.sendMailVerification(recipient_email);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void verifyCodeTest(){
        String inputCode = "P94NBvWTCR";
        assert (sendMailService.verifyCode(recipient_email,inputCode).equals(AuthenticationStatus.SUCCESS) );
    }
}