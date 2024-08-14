package com.project.FreeCycle.Service;

import com.project.FreeCycle.Repository.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class NaverUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;
    private Map<String, Object> response;

    public NaverUserDetails(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.response = (Map<String, Object>) attributes.get("response");
        log.info("Naver attributes: {}", attributes);
        log.info("Naver response: {}", response);
    }

    @Override
    public String getProvider() {
        return "Naver";
    }


    @Override
    public String getProviderId() {
        if (response != null && response.containsKey("id")) {
            return (String) response.get("id");
        } else if (attributes.containsKey("id")) {
            return (String) attributes.get("id");
        } else {
            log.error("Provider ID를 찾을 수 없습니다.");
            return null;
        }
    }

    @Override
    public String getEmail(){
        return (String) response.get("email");
    }

    @Override
    public String getName() {
        return (String) response.get("name");
    }

    @Override
    public String getNickname() {
        return (String) response.get("nickname");
    }
}
