package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.CustomUserDetail;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User findUserId = userRepository.findByUserId(userId);

        if (findUserId != null) {
            return new CustomUserDetail(findUserId,null);
        }
        return null;
    }
}
