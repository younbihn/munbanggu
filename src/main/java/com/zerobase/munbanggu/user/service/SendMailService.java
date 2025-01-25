package com.zerobase.munbanggu.user.service;

import static com.zerobase.munbanggu.common.type.RedisTime.MAIL_VALID;

import com.zerobase.munbanggu.common.exception.InvalidTokenException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.common.exception.VerificationException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.common.util.RedisUtil;
import com.zerobase.munbanggu.user.dto.MailDto;
import com.zerobase.munbanggu.user.dto.MailVerificationDto;
import com.zerobase.munbanggu.user.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
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
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String id;

    @Value("${secret.recipient}")
    private String sender;

    /**
     * 템플릿 생성
     *
     * @param to    수신자
     * @param path  템플릿 위치
     * @return MailDto
     */
    private MailDto setTemplate(String to,String path){
        String subject;
        String[] split_path = path.split("/");
        String template = split_path[split_path.length-1];

        MailDto mail = new MailDto();
        try {
            File input = new File(path);
            subject = Jsoup.parse(input,MAIL_CHARSET).getElementsByTag("title").iterator().next().text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mail.setTo(to);
        mail.setFrom(sender);
        mail.setSubject(subject);
        mail.setTemplate(template);
        return mail;
    }

    /**
     * 메일 생성
     *
     * @param dto           메일dto
     * @param emailValue    이메일인자(HashMap<String,String>
     * @return MimeMessage
     */
    private MimeMessage createMessage(MailDto dto, HashMap<String,String> emailValue){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.addRecipients(Message.RecipientType.TO, dto.getTo());
            message.setSubject(dto.getSubject());
            message.setText(setContext(emailValue, dto.getTemplate()), MAIL_CHARSET);
            message.setFrom(new InternetAddress(id,"munbanggu"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  message;
    }

    /**
     * emailValue를 html템플릿에 매핑
     *
     * @param emailValue  이메일인자
     * @param template    템플릿
     * @return springTemplateEngine.process(template, context)
     */
    private String setContext(HashMap<String,String> emailValue, String template){
        Context context = new Context();
        emailValue.forEach((key, value)->{
            context.setVariable(key, value);
        });
        return springTemplateEngine.process(template, context);
    }

    /**
     * 이메일 전송
     *
     * @param email 이메일
     * @return AuthenticationStatus(SUCCESS/FAIL)
     */
    public boolean sendMailVerification(String email) {
        final String  TEMPLATE_NAME = "mailVerification.html";
        final String PATH = "src/main/resources/templates/"+TEMPLATE_NAME;

        String token = authService.createVerificationToken(email,MAIL_VALID.getTime());
        String link = "http://localhost:8080/api/auth/verify-email?email="+email+"&token="+token;

        HashMap<String,String> emailValue = new HashMap<>();
        emailValue.put("link",link);
        log.info("\n>>> emailValue : "+emailValue);

        try{
            MailDto dto = setTemplate( email, PATH);
            MimeMessage mimeMessage = createMessage(dto,emailValue);
            javaMailSender.send(mimeMessage);
            redisUtil.setData(email, token , MAIL_VALID.getTime());
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * 이메일 링크 인증
     *
     * @param mailVerificationDto - email,token
     * @return 성공여부(T/F)
     */
    public boolean verifyEmail(MailVerificationDto mailVerificationDto) {
        String saved_token = redisUtil.getData(mailVerificationDto.getEmail());

        // redis에 저장되어 있지 않으면 오류 반환
        if (saved_token==null)
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);

        // redis에 저장된 토큰과 일치하지 않으면 오류 반환
        if (!saved_token.equals(mailVerificationDto.getToken()))
            throw new VerificationException(ErrorCode.INVALID_EMAIL);

        // 회원이 아니면 오류 반환
        userRepository.findByEmail(mailVerificationDto.getEmail())
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.EMAIL_NOT_EXIST));

        // redis에서 정보삭제
        redisUtil.deleteData(mailVerificationDto.getEmail());

        return true;
    }
}

