package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.UserConverter;
import com.project.FreeCycle.Dto.UserDTO;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordUtil passwordUtil = new PasswordUtil(new BCryptPasswordEncoder());

    public User UserSave(UserDTO userDTO){
        // 중복회원 검사 추가 해야 함.
        if (userRepository.existsByUserId(userDTO.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        User user = UserConverter.toDomain(userDTO);

        if (user.getPassword() != null) {
            user.setPassword(passwordUtil.encodePassword(user.getPassword())); // 비밀번호 인코딩
        }

        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }
}
