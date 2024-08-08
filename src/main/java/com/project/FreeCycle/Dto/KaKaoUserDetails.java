package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Repository.OAuth2UserInfo;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KaKaoUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;


    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "Kakao";
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("nickname");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
