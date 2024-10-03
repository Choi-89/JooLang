package com.project.FreeCycle.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 스코프 정의
//        Map<String, String> scopes = new HashMap<>();
        Scopes scopes = new Scopes();
        scopes.put("profile", "Allows profile access"); // application.properties의 profile 스코프
        scopes.put("email", "Allows email access");     // application.properties의 email 스코프
;

        // OAuthFlow 설정 (Authorization Code Grant 방식)
        OAuthFlow authorizationCodeFlow = new OAuthFlow()
                .authorizationUrl("https://nid.naver.com/oauth2.0/authorize")
                .tokenUrl("https://nid.naver.com/oauth2.0/token")
                .scopes(scopes);

        // OAuthFlows 설정
        OAuthFlows oauthFlows = new OAuthFlows();
        oauthFlows.authorizationCode(authorizationCodeFlow);

        // SecurityScheme 설정
        SecurityScheme oAuth2Scheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(oauthFlows);

        // OpenAPI 객체 생성 및 SecurityRequirement 설정
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("OAuth2"))
                .components(new Components().addSecuritySchemes("OAuth2", oAuth2Scheme))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Your API Documentation")
                        .version("v1.0")
                        .description("This is the API documentation with OAuth2 Authentication"));
    }
}