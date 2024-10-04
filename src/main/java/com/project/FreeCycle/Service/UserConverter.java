package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("UserDTO 변환 시작: {}", userDTO);

        User user = new User();

        if (userDTO.getUserId() == null) {
            log.error("userId가 null입니다.");
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }

        user.setUserId(userDTO.getUserId());

        user.setPhoneNum(userDTO.getPhoneNum());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setProviderId(userDTO.getProviderId());
        user.setProvider(userDTO.getProvider());
        user.setNickname(userDTO.getNickname());
        user.setName(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        log.info("User 변환 완료: {}", user);
        return user;
    }


}
