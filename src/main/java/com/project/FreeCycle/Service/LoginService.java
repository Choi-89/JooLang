package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // 로그인 성공 유무
    public boolean login(String userId, String password) {

        User user = userRepository.findByUserId(userId);

        if (user != null) {
            return bCryptPasswordEncoder.matches(password, user.getPassword());
        }

        return false;
    }

}
