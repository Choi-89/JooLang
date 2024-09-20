package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private long Id;
    private String nickname;
    private String password;
    private String username;
    private String userId;
    private String email;
    private String phoneNumber;
    private String provider;
    private String role;
    private String providerId;

    public UserDTO(String userId, String username, String nickname, String email, String role, String provider, String providerId,
                   String phoneNumber, long Id) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.phoneNumber = phoneNumber;
    }




}
