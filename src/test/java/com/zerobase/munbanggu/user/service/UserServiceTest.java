package com.zerobase.munbanggu.user.service;

import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
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

  @Test
  @AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
  public void saveTest(){
    System.out.println("\nSAVING--");

    User user = new User();
    user.setEmail(email);
    user.setNickname("KIM");
    user.setPhone("01011111111");
    user.setProfileImageUrl("c:/");
    userRepository.save(user);
  }

  @Test
  public void getInfoTest(){
    assert (userService.getInfo(email).getEmail().equals(email));
  }

}