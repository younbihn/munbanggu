package com.zerobase.munbanggu.user.service;

import com.zerobase.munbanggu.user.controller.AuthController;
import com.zerobase.munbanggu.user.dto.FindUserInfoDto;
import com.zerobase.munbanggu.user.dto.ResetPwDto;
import com.zerobase.munbanggu.user.dto.SmsVerificationInfo;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.type.AuthenticationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
  private final String email = "kim@naver.com";

  @Autowired
  UserService userService;
  @Autowired
  UserRepository userRepository;
  @Autowired
  AuthController authController;

  @Test
  @AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
  public void saveTest(){
    System.out.println("\nSAVING--");

    User user = new User();
    user.setEmail(email);
    user.setNickname("KIM");
    user.setName("GILDONG");
    user.setPhone("01011111111");
    user.setProfileImageUrl("c:/");
    userRepository.save(user);
  }

  @Test
  public void getInfoTest(){
    assert (userService.getInfo(email).getEmail().equals(email));
  }

  @Test
  public void findIdTest(){
    User user = new User();
    user.setEmail(email);
    user.setNickname("KIM");
    user.setName("GILDONG");
    user.setPhone("010-1111-1111");
    System.out.println("\n>> UserSAVED ");

    FindUserInfoDto findUserInfoDto = new FindUserInfoDto();
    findUserInfoDto.setName(user.getName());
    findUserInfoDto.setPhone(user.getPhone());

    System.out.println("\n>>>>>>>>>>> "+ findUserInfoDto.getEmaill() + " " +findUserInfoDto.getPhone());

    String token = userService.requestFindId(findUserInfoDto);
    System.out.println("\n>>>>>>>>>>> token"+ token);

    // 핸드폰 인증 요청 테스트
    String input = "1234";
    SmsVerificationInfo smsVerificationInfo = new SmsVerificationInfo();
    smsVerificationInfo.setVerificationCode(input);
    smsVerificationInfo.setToken(token);
    smsVerificationInfo.setPhone(user.getPhone());

    System.out.println("\n----------- TEST:: VERIFY ----------");
    System.out.println(authController.verifySMS(smsVerificationInfo));
  }

  @Test
  public void resetPwTest(){
    User user = new User();
    user.setEmail(email);
    user.setNickname("KIM");
    user.setName("GILDONG");
    user.setPhone("01011111111");

    FindUserInfoDto findUserInfoDto = new FindUserInfoDto();

    ResetPwDto resetPwDto = new ResetPwDto();
    findUserInfoDto.setName("GILDONG");
    findUserInfoDto.setPhone("010-1111-1111");

    String token = userService.requestResetPw(findUserInfoDto);

    String input = "1234";  // 핸드폰 인증코드
    SmsVerificationInfo smsVerificationInfo = new SmsVerificationInfo();
    smsVerificationInfo.setVerificationCode(input);
    smsVerificationInfo.setToken(token);
    smsVerificationInfo.setPhone(user.getPhone());

    System.out.println("\n----------- TEST:: VERIFY ----------");
    System.out.println(authController.verifySMS(smsVerificationInfo));

    System.out.println("\n----- complete verification ----- ");

    resetPwDto.setNewPassword("newPassword");
    resetPwDto.setInputCode("1234");
    resetPwDto.setToken("abc123");

    assert(userService.verifyResetPw(resetPwDto).equals(AuthenticationStatus.SUCCESS));

  }
}