package com.zerobase.munbanggu.config.auth;

import static com.zerobase.munbanggu.user.exception.ErrorCode.INVALID_TOKEN;

import com.zerobase.munbanggu.user.exception.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
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

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessTokenOrRefreshToken(CustomOAuth2User oAuth2User, Long expirationTimeInSeconds) {
        String email = oAuth2User.getUser().getEmail();

        String authority = "";
        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        if (!authorities.isEmpty()) {
            authority = authorities.iterator().next().getAuthority();
        }

        Date expiration = new Date(System.currentTimeMillis() + expirationTimeInSeconds);
        return generateToken(email, authority, expiration);
    }

    public String generateAccessToken(CustomOAuth2User oAuth2User) {
        return generateAccessTokenOrRefreshToken(oAuth2User, accessTokenExpirationTimeInSeconds);
    }

    public String generateRefreshToken(CustomOAuth2User oAuth2User) {
        return generateAccessTokenOrRefreshToken(oAuth2User, refreshTokenExpirationTimeInSeconds);
    }

    private String generateToken(String email, String authority, Date expiration) {
        Map<String, String> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("authority", authority);

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public void addAccessRefreshTokenToResponseHeader(HttpServletResponse response, String accessToken,
            String refreshToken) {

        response.addHeader(ACCESS_TOKEN_KEY, AUTHORIZATION_PREFIX + accessToken);
        response.addHeader(REFRESH_TOKEN_KEY, AUTHORIZATION_PREFIX + refreshToken);
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
        } catch (JwtException e) {
            log.error("JwtException = " + e.getMessage());
            throw new InvalidTokenException(INVALID_TOKEN.getMessage());
        }
    }

    public String getRawToken(String token) {
        token = token.replace(AUTHORIZATION_PREFIX, "");
        return token;
    }

    public Authentication getAuthentication(String token) {
        PrincipalDetails principalDetails = new PrincipalDetails(getEmail(token), getAuthority(token));
        return new UsernamePasswordAuthenticationToken(principalDetails, "",
                principalDetails.getAuthorities());


    }


}
