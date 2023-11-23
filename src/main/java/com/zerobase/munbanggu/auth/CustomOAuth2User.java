package com.zerobase.munbanggu.auth;

import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.type.AuthProvider;
import com.zerobase.munbanggu.user.type.LoginType;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final transient User user;
    private final AuthProvider authProvider;
    private final LoginType loginType;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     *
     * @param authorities      the authorities granted to the user
     * @param attributes       the attributes about the user
     * @param nameAttributeKey the key used to access the user's &quot;name&quot; from {@link #getAttributes()}
     */
    public CustomOAuth2User(User user, AuthProvider authProvider,LoginType loginType, Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey) {
        super(authorities, attributes, nameAttributeKey);
        this.user = user;
        this.authProvider = authProvider;
        this.loginType = loginType;
    }
}
