package com.project.FreeCycle.Api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Getter
@Component(value = "naverApi")
public class NaverApi {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String NaverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String NaverRedirectUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String NaverClientSecret;

}


