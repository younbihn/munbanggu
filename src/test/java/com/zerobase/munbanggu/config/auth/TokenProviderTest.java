package com.zerobase.munbanggu.config.auth;

import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.type.AuthProvider;
import com.zerobase.munbanggu.user.type.Role;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void generateTokenTest() {
        // given
        User user = User.builder()
                .authProvider(AuthProvider.KAKAO)
                .email("test1234@gmail.com")
                .role(Role.USER)
                .build();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("properties", new Object());
        attributes.put("id", new Object());
        attributes.put("kakao_account", new Object());
        String nameAttributeKey = "id";

        CustomOAuth2User oAuth2User = new CustomOAuth2User(user, AuthProvider.KAKAO,
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())), attributes, nameAttributeKey);

        // when
        String token = tokenProvider.generateAccessToken(oAuth2User);

        // then
        Assertions.assertThat(token).isNotNull();
    }
}