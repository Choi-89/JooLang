package com.project.FreeCycle.Api;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;


@Getter
@Slf4j
@Component(value = "kakaoApi")
public class KakaoApi {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KakaoClientSecret;


}
