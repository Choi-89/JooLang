package com.project.FreeCycle.Api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Slf4j
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


