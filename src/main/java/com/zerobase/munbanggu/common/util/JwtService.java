package com.zerobase.munbanggu.util;

import com.zerobase.munbanggu.user.dto.UserInfo;
import com.zerobase.munbanggu.user.type.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private final String SECRET_KEY = "SecretKey_ALSO32Byte";
    private final long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 2; // 만료 시간 : 2일

    private final Set<String> blacklistedTokens = new HashSet<>();

    public void logout(String token) {
        blacklistedTokens.add(token); // 블랙리스트에 토큰 추가
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public String createToken(Long id, String email, Role userType) {
        Claims claims = Jwts.claims()
                .setId(id.toString())
                .setSubject(email);

        claims.put("roles", userType);
        Date nowTime = new Date();

        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(nowTime)
                .setExpiration(new Date(nowTime.getTime() + TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 유저 정보(UserInfo) 가져오기
     *
     * @Param: JWT Token String
     * @Return: UserInfo(id, email)
     */
    public UserInfo getUserInfoFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new UserInfo(
                Long.valueOf(claims.getId()), claims.getSubject()
        );
    }

    /**
     * 토큰을 분석하여 유효 여부를 확인
     *
     * @Param: JWT Token String
     * @Return: 토큰의 유효 여부
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> parsedClaimsJws = Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token);
            Claims claims = parsedClaimsJws.getBody();


            return !parsedClaimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 고유 id 값 얻어오기
     *

     */
    public Long getIdFromToken(String token) {
        return getUserInfoFromToken(token.substring(7)).getId();
    }
}
