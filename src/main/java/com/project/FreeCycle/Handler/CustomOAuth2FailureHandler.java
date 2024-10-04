package com.project.FreeCycle.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String redirectUri = request.getParameter("redirect_uri");

        if (redirectUri != null && !redirectUri.isEmpty()) {
            // 리다이렉트가 필요한 경우, 오류 메시지를 URL 파라미터로 추가해 리다이렉트
            response.sendRedirect(redirectUri + "?error=" + exception.getMessage());
        } else {
            // 리다이렉트 URI가 없으면 JSON 응답을 반환
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "OAuth2 로그인 실패: " + exception.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(jsonResponse));
            response.getWriter().flush();
        }
    }
}
