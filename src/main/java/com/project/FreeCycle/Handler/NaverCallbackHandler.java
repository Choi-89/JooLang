package com.project.FreeCycle.Handler;

import com.project.FreeCycle.Api.NaverApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class NaverCallbackHandler {

    @Autowired
    NaverApi naverApi;

    private static final String FIXED_STATE = "3s0asdt0";

    @GetMapping("/auth/naver/login/callback")
    public String handleNaverCallback(@RequestParam("code") String code,
                                      @RequestParam("state") String state,
                                      HttpServletRequest request) {
        log.info("네이버 로그인 콜백 호출됨, code: {}, state: {}", code, state);

        if (!FIXED_STATE.equals(state)) {
            log.error("잘못된 state 값입니다.");
            return "redirect:/home/login?error=true";
        }

        String accessToken = getAccessToken(code, state);

        if (accessToken == null) {
            log.error("액세스 토큰을 가져오지 못했습니다.");
            return "redirect:/home/login?error=true";
        }
        // 이후 액세스 토큰을 사용하여 사용자 정보를 받아오는 로직을 추가합니다.

        Map<String, Object> userInfo = getUserInfo(accessToken);

        if (userInfo == null) {
            log.error("사용자 정보를 가져오지 못했습니다.");
            return "redirect:/home/login?error=true";
        }

        log.info("로그인 성공, 사용자 정보: {}", userInfo);

        // 세션에 사용자 정보 저장
        request.getSession().setAttribute("user", userInfo);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);


        log.info("SecurityContextHolder 설정 완료, 인증 정보: {}", SecurityContextHolder.getContext().getAuthentication());


        return "redirect:/home_user";
    }

    private String getAccessToken(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tokenUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", naverApi.getNaverClientId())
                .queryParam("client_secret", naverApi.getNaverClientSecret())
                .queryParam("code", code)
                .queryParam("state", state);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<>() {});

        log.info("토큰 요청 응답: {}", response);


        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            log.info("토큰 응답 바디: {}", body);
            return (String) body.get("access_token");
        } else {
            log.error("액세스 토큰 요청 실패: {}, 응답 바디: {}", response.getStatusCode(), response.getBody());
            return null;
        }
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {});

        log.info("사용자 정보 요청 응답: {}", response);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (Map<String, Object>) response.getBody().get("response");
        } else {
            log.error("사용자 정보 요청 실패: {}", response.getStatusCode());
            return null;
        }
    }
}
