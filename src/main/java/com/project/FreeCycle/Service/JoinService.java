package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User UserSave(User user){
        // 중복회원 검사 추가 해야 함.
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));  // 비밀번호 인코딩
        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }
}
