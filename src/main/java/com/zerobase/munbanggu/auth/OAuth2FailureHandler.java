package com.zerobase.munbanggu.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.munbanggu.common.dto.ErrorResponse;
import com.zerobase.munbanggu.common.exception.DuplicatedEmailConflictException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof DuplicatedEmailConflictException) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);

            ObjectMapper om = new ObjectMapper();
            String jsonResponse = om.writeValueAsString(
                    new ErrorResponse(ErrorCode.EMAIL_CONFLICT, ErrorCode.EMAIL_CONFLICT.getDescription()));

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

        }
    }
}
