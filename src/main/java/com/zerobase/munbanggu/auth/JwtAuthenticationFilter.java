package com.zerobase.munbanggu.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.munbanggu.dto.ErrorResponse;
import com.zerobase.munbanggu.type.ErrorCode;
import com.zerobase.munbanggu.user.exception.InvalidTokenException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isLoginRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            if (authorizationHeader != null) {
                String token = authorizationHeader.replace("Bearer ", "");
                tokenProvider.validateToken(token);
                setAuthToSecurityContextHolder(tokenProvider.getRawToken(authorizationHeader));
            }
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_TOKEN, e.getMessage());
            String jsonErrorResponse = new ObjectMapper().writeValueAsString(errorResponse);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonErrorResponse);
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    private void setAuthToSecurityContextHolder(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/sign-in");
    }
}
