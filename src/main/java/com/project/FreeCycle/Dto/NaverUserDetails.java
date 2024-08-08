package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Repository.OAuth2UserInfo;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class NaverUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "Naver";
    }


    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }


    @Override
    public String getEmail() {
        return  (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    public String getNickname() {
        return (String) attributes.get("nickname");
    }


}
