package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Domain.User;

public class UserConverter {

    public static UserDTO toDTO(User user){
        return new UserDTO(
                user.getUserId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNum(),
                user.getRole(),
                user.getProviderId(),
                user.getProvider(),
                user.getId()
        );
    }

    public static User toDomain(UserDTO userDTO){
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setPhoneNum(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setProviderId(userDTO.getProviderId());
        user.setProvider(userDTO.getProvider());
        user.setNickname(userDTO.getNickname());
        user.setName(userDTO.getUsername());
        return user;
    }



}
