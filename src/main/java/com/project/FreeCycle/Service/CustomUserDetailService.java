package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.CustomUserSecurityDetail;
import com.project.FreeCycle.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 기존 LoginService 역할을 Security에서 수행해줌
 
// 일반 form 로그인 , CustomUserSecurityDetail
@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);

        if(user != null){
            return new CustomUserSecurityDetail(user);
        }

        throw new UsernameNotFoundException("유저를 찾을 수 없습니다. userId : " + userId);
    }
}
