package com.zerobase.munbanggu.config.auth;

import static com.zerobase.munbanggu.type.ErrorCode.*;

import com.zerobase.munbanggu.user.exception.DuplicatedEmailConflictException;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import com.zerobase.munbanggu.user.service.RedisUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @Value("${jwt.expiration.refresh-token-seconds}")
    private Long refreshTokenExpirationTimeInSeconds;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        User user = userRepository.findById(oAuth2User.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException(
                        EMAIL_NOT_EXISTS.getDescription()));

        checkUserAuthProvider(user, oAuth2User);

        String accessToken = tokenProvider.generateAccessToken(oAuth2User);
        String refreshToken = tokenProvider.generateRefreshToken(oAuth2User);

        saveRefreshTokenInRedis(oAuth2User, refreshToken);

        log.info("redisUtil.getData(): " + redisUtil.getData("RT:"+oAuth2User.getUser().getEmail()));

        tokenProvider.addAccessRefreshTokenToResponseHeader(response, accessToken, refreshToken);
    }

    private void checkUserAuthProvider(User user, CustomOAuth2User oAuth2User) {
        if (user.getAuthProvider() == null || !user.getAuthProvider().equals(oAuth2User.getAuthProvider())) {
            throw new DuplicatedEmailConflictException(EMAIL_CONFLICT);
        }
    }

    private void saveRefreshTokenInRedis(CustomOAuth2User oAuth2User, String refreshToken) {
        refreshToken = tokenProvider.getRawToken(refreshToken);
        redisUtil.setData("RT:" + oAuth2User.getUser().getEmail(), refreshToken,
                refreshTokenExpirationTimeInSeconds);
    }
}
