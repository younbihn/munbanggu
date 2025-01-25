package com.zerobase.munbanggu.config.auth;

import com.zerobase.munbanggu.auth.CustomOAuth2User;
import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.type.AuthProvider;
import com.zerobase.munbanggu.user.type.LoginType;
import com.zerobase.munbanggu.user.type.Role;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest
@ComponentScan(basePackages = "com.zerobase.munbanggu")
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;
    private User user;
    private Map<String, Object> attributes;
    private CustomOAuth2User oAuth2User;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .authProvider(AuthProvider.KAKAO)
                .loginType(LoginType.KAKAO)
                .email("test1234@gmail.com")
                .role(Role.USER)
                .build();

        attributes = new HashMap<>();
        attributes.put("properties", new Object());
        attributes.put("id", new Object());
        attributes.put("kakao_account", new Object());
        String nameAttributeKey = "id";

        oAuth2User = new CustomOAuth2User(user, AuthProvider.KAKAO, LoginType.KAKAO,
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())), attributes, nameAttributeKey);
    }

    @Test
    void generateTokenKakaoTest() {
        // given

        // when
        String token = tokenProvider.generateAccessToken(oAuth2User);

        // then
        Assertions.assertThat(token).isNotNull();
    }

    @Test
    void generateTokenTest() {
        // given
        User user = User.builder()
                .id(1L)
                .authProvider(AuthProvider.KAKAO)
                .loginType(LoginType.KAKAO)
                .email("test1234@gmail.com")
                .role(Role.USER)
                .build();

        // when
        String token = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

        // then
        Assertions.assertThat(token).isNotNull();
    }

}