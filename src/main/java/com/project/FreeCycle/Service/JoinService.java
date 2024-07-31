package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JoinService {
    @Autowired
    private UserRepository userRepository;

    public void save(User user){
        
        // 중복회원 검사 추가 해야 함.


        userRepository.save(user);
        
    }
}
