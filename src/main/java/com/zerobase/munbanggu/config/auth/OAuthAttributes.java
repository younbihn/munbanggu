package com.zerobase.munbanggu.config.auth;

import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.type.AuthProvider;
import com.zerobase.munbanggu.user.type.Role;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String nickname;
    private final String email;
    private final Role role;
    private final AuthProvider authProvider;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String nickname, String email,
             Role role, AuthProvider authProvider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.authProvider = authProvider;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
            Map<String, Object> attributes) {
        if (registrationId.equals("Kakao")) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofKakao(userNameAttributeName, attributes);

    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) attributes.get("properties");
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .attributes(attributes)
                .nickname((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .authProvider(AuthProvider.KAKAO)
                .role(Role.USER)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .role(role)
                .authProvider(authProvider)
                .build();
    }

}
