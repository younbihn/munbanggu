package com.zerobase.munbanggu.auth;

import static com.zerobase.munbanggu.common.type.ErrorCode.INVALID_TOKEN;

import com.zerobase.munbanggu.common.exception.InvalidTokenException;
import com.zerobase.munbanggu.user.type.Role;
import com.zerobase.munbanggu.common.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private Key key;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access-token-seconds}")
    private Long accessTokenExpirationTimeInSeconds;

    @Value("${jwt.expiration.refresh-token-seconds}")
    private Long refreshTokenExpirationTimeInSeconds;
    private static final String AUTHORIZATION_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_KEY = "Access-Token";
    public static final String REFRESH_TOKEN_KEY = "Refresh-Token";
    private SecretKey secretKey;

    private final RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateAccessTokenOrRefreshToken(Long id, String email, Role role, Long expirationTimeInSeconds) {

        String authority = role.getKey();

        Date expiration = new Date(System.currentTimeMillis() + expirationTimeInSeconds);
        return generateToken(id, email, authority, expiration);
    }

    public String generateAccessTokenOrRefreshToken(CustomOAuth2User oAuth2User, Long expirationTimeInSeconds) {
        String email = oAuth2User.getUser().getEmail();
        Long id = oAuth2User.getUser().getId();

        String authority = "";
        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        if (!authorities.isEmpty()) {
            authority = authorities.iterator().next().getAuthority();
        }

        Date expiration = new Date(System.currentTimeMillis() + expirationTimeInSeconds);
        return generateToken(id, email, authority, expiration);
    }

    public String generateAccessToken(Long id, String email, Role role) {
        return generateAccessTokenOrRefreshToken(id, email, role, accessTokenExpirationTimeInSeconds);
    }

    public String generateAccessToken(CustomOAuth2User oAuth2User) {
        return generateAccessTokenOrRefreshToken(oAuth2User, accessTokenExpirationTimeInSeconds);
    }

    public String generateRefreshToken(Long id, String email, Role role) {
        return generateAccessTokenOrRefreshToken(id, email, role, refreshTokenExpirationTimeInSeconds);
    }

    public String generateRefreshToken(CustomOAuth2User oAuth2User) {
        return generateAccessTokenOrRefreshToken(oAuth2User, refreshTokenExpirationTimeInSeconds);
    }

    private String generateToken(Long id, String email, String authority, Date expiration) {
        Map<String, String> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("authority", authority);
        claims.put("id", id.toString());

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void addAccessRefreshTokenToResponseHeader(HttpServletResponse response, String accessToken,
            String refreshToken) {

        response.addHeader(ACCESS_TOKEN_KEY, AUTHORIZATION_PREFIX + accessToken);
        response.addHeader(REFRESH_TOKEN_KEY, AUTHORIZATION_PREFIX + refreshToken);
    }

    public Long getId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(getRawToken(token))
                    .getBody();

            return Long.parseLong(claims.get("id").toString());
        } catch (JwtException | NumberFormatException | NullPointerException e) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(getRawToken(token)).getBody()
                .get("email")
                .toString();
    }

    public Long getExpirationTimeInSeconds(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(getRawToken(token)).getBody()
                .getExpiration()
                .getTime();
    }

    public String getAuthority(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(getRawToken(token)).getBody()
                .get("authority").toString();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(getRawToken(token));
            return true;
        } catch (Exception e) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }
    }

    public String getRawToken(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }
        return authHeader.replace(AUTHORIZATION_PREFIX, "");
    }

    public Authentication getAuthentication(String token) {
        PrincipalDetails principalDetails = new PrincipalDetails(getEmail(token), getAuthority(token));
        return new UsernamePasswordAuthenticationToken(principalDetails, "",
                principalDetails.getAuthorities());
    }

    public void setLogoutTokenInRedis(String token) {
        if (redisUtil.getData(getEmail(token)) != null) {
            redisUtil.deleteData(getEmail(token));
        }
        Long expirationTimeInSeconds = getExpirationTimeInSeconds(token);
        redisUtil.setData("BLACK:" + getRawToken(token), "logout", expirationTimeInSeconds);
    }

    public void saveRefreshTokenInRedis(CustomOAuth2User oAuth2User, String refreshToken) {
        refreshToken = getRawToken(refreshToken);
        redisUtil.setData("RT:" + oAuth2User.getUser().getEmail(), refreshToken,
                refreshTokenExpirationTimeInSeconds);
    }

    public void saveRefreshTokenInRedis(String email, String refreshToken) {
        refreshToken = getRawToken(refreshToken);
        redisUtil.setData("RT:" + email, refreshToken,
                refreshTokenExpirationTimeInSeconds);
    }


}