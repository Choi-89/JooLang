package com.project.FreeCycle.Repository;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
    String getNickname();
    String getEmail();
    String getMobile();
}
