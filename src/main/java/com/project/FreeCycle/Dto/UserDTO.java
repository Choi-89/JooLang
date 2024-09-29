package com.project.FreeCycle.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class UserDTO {

    private long Id;
    private String nickname;
    private String password;
    private String username;
    private String userId;
    private String email;
    private String phoneNum;
    private String provider;
    private String role;
    private String providerId;


    public UserDTO(String userId, String username, String nickname, String email, String role, String provider, String providerId,
                   String phoneNum, long Id) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.phoneNum = phoneNum;
    }
}
