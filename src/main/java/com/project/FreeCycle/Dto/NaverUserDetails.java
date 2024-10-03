package com.project.FreeCycle.Dto;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

// OAuth2를 통해 로그인한 사용자의 정보를 다루기 위함
@Slf4j
public class NaverUserDetails implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> response;

    public NaverUserDetails(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "Naver";
    }


    @Override
    public String getProviderId() {
        if (response != null && response.containsKey("id")) {
            return response.get("id").toString();
        } else if (attributes.containsKey("id")) {
            return attributes.get("id").toString();
        } else {
            log.error("Provider ID를 찾을 수 없습니다.");
            return null;
        }
    }

    @Override
    public String getEmail(){
        return response.get("email").toString();
    }

    @Override
    public String getMobile() {
        return response.get("mobile").toString();
    }

    @Override
    public String getName() {
        return response.get("name").toString();
    }

    @Override
    public String getNickname() {
        return response.get("nickname").toString();
    }

}
