package com.zerobase.munbanggu.user.service;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.util.RedisUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisUtil redisUtil;
    private final TokenProvider tokenProvider;

    public void logout(String token) {

        if (tokenProvider.validateToken(token)) {
            String email = tokenProvider.getEmail(token);
            Long expirationTimeInSeconds = tokenProvider.getExpirationTimeInSeconds(token);

            if (redisUtil.getData(email) != null) {
                redisUtil.deleteData(email);
            }
            redisUtil.setData("BLACK:" + tokenProvider.getRawToken(token), "logout", expirationTimeInSeconds);
        }
    }

    public String createVerificationToken(String key, long time){
        String token = UUID.randomUUID().toString();
        redisUtil.setData(key, token, time);
        return token;
    }
}
