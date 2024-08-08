package com.project.FreeCycle.Handler;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.project.FreeCycle.Api.KakaoApi;
import jakarta.annotation.PostConstruct;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class KakaoCallbackHandler {

    @Autowired
    KakaoApi kakaoApi;

    private static final String FIXED_STATE = "3s0asdt0";

    @PostConstruct
    public void logValues() {
        log.info("Kakao Client ID: {}", kakaoApi.getKakaoClientId());
        log.info("Kakao Redirect URI: {}", kakaoApi.getKakaoRedirectUri());
        log.info("Kakao Client Secret: {}", kakaoApi.getKakaoClientSecret());
    }

    @GetMapping("/auth/kakao/login/callback")
    public String handleKakaoCallback(@RequestParam("code") String code,
                                      @RequestParam("state") String state,
                                      HttpServletRequest request){
        log.info("카카오 로그인 콜백 호출됨, code: {}, state: {}", code, state);

        if (!FIXED_STATE.equals(state)) {
            log.error("잘못된 state 값입니다.");
            return "redirect:/home/login?error=true";
        }

        String accessToken = getAccessToken(code, state);

        if (accessToken == null) {
            log.error("액세스 토큰을 가져오지 못했습니다.");
            return "redirect:/home/login?error=true";
        }

        // 사용자 정보 요청
        Map<String, Object> userInfo = getUserInfo(accessToken);

        if (userInfo == null) {
            log.error("카카오 사용자 정보를 가져오지 못했습니다.");
            return "redirect:/home/login?error=true";
        }

        log.info("카카오 로그인 성공, 사용자 정보: {}", userInfo);

        // 세션에 사용자 정보 저장
        request.getSession().setAttribute("user", userInfo);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);


        log.info("SecurityContextHolder 설정 완료, 인증 정보: {}", SecurityContextHolder.getContext().getAuthentication());


        return "redirect:/home_user";

    }


    public String getAccessToken(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        //HttpHeaders 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoApi.getKakaoClientId()); // 클라이언트 ID 설정
        params.add("redirect_uri", kakaoApi.getKakaoRedirectUri()); // 리다이렉트 URI 설정
        params.add("code", code); // 인증 코드 설정
        params.add("state", state); // 상태 값 설정
        params.add("client_secret", kakaoApi.getKakaoClientSecret()); // 클라이언트 시크릿 설정

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        log.info("카카오 토큰 요청 응답: {}", response);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("카카오 엑세스 토큰 요청 성공");
            JsonObject responseBody = JsonParser.parseString(response.getBody()).getAsJsonObject();
            return responseBody.get("access_token").getAsString(); // 액세스 토큰 반환
        } else {
            log.error("카카오 액세스 토큰 요청 실패: {}", response.getStatusCode());
            return null;
        }
    }


    public Map<String, Object> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {});

        log.info("카카오 사용자 정보 요청 응답: {}", response);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            log.error("카카오 사용자 정보 요청 실패: {}", response.getStatusCode());
            return null;
        }
    }

}
