package com.zerobase.munbanggu.user.service;
import static com.zerobase.munbanggu.user.type.RedisTime.MAIL_VALID;
import com.zerobase.munbanggu.user.dto.MailDto;
import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import java.util.HashMap;
import org.apache.commons.lang3.RandomStringUtils;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
@Component
public class SendMailService {
    private final SpringTemplateEngine springTemplateEngine;
    private final String MAIL_CHARSET = "utf-8";
    private final int CODE_LENGTH = 7;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String id;

    @Value("${secret.recipient}")
    private String sender;

    private MailDto setTemplate(String to, String subject, String template, HashMap<String,String> emailValue){
        MailDto mail = new MailDto();
        mail.setTo(to);
        mail.setFrom(sender);
        mail.setSubject(subject);
        mail.setTemplate(template);
        mail.setEmailValue(emailValue);
        return mail;
    }

    private MimeMessage createMessage(MailDto dto){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.addRecipients(Message.RecipientType.TO, dto.getTo());
            message.setSubject(dto.getSubject());
            message.setText(setContext(dto.getEmailValue(), dto.getTemplate()), MAIL_CHARSET,"html");
            message.setFrom(new InternetAddress(id,"munbanggu"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  message;
    }

    private String setContext(HashMap<String,String> emailValue, String template){
        Context context = new Context();
        emailValue.forEach((key, value)->{
            context.setVariable(key, value);
        });
        return springTemplateEngine.process(template, context);
    }

    public AuthenticationStatus sendMailVerification(String email){
        String code = RandomStringUtils.random(CODE_LENGTH,true,true);
        log.info("\n>>>>>> sender: " + sender + "code: "+code);

        String subject = "[문방구] 메일 인증 안내";
        HashMap<String,String> emailValue = new HashMap<>();
        emailValue.put("code",code);

        log.info("\n emailValue : "+emailValue);
        try{
            MailDto dto = setTemplate(email,subject, "mailVerification",emailValue);
            MimeMessage mimeMessage = createMessage(dto);
            javaMailSender.send(mimeMessage);
            redisUtil.setData(email, code, MAIL_VALID.getTime());
            return AuthenticationStatus.SUCCESS;}
        catch (Exception e){
            return AuthenticationStatus.FAIL;
        }
    }

    public AuthenticationStatus verifyCode(String email, String input) {
        String code = "";

        try {
            code = redisUtil.getData(email);
            log.info("\ncode : " + code + " input_code : " + input + " same?: " + code.equals(input));
        } catch (Exception e) {
            log.info(e.getMessage());
            return AuthenticationStatus.FAIL;

        }

        if (code.equals(input)) {
            return AuthenticationStatus.SUCCESS;
        }
        return AuthenticationStatus.FAIL;
    }
}

