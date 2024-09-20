package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.UserConverter;
import com.project.FreeCycle.Dto.UserDTO;
import com.project.FreeCycle.Repository.LocationRepository;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService{

    @Autowired
    private UserRepository userRepository;

    private final PasswordUtil passwordUtil = new PasswordUtil(new BCryptPasswordEncoder());

    // 비밀번호 체크
    public boolean checkPassword(String userId,String password){
        User user = userRepository.findByUserId(userId);

        if (user!=null && passwordUtil.matchesPassword(password,user.getPassword())){
            return true;
        }
        return false;
    }

    // 유저 삭제
    @Transactional
    public boolean deleteUser(String userId){
        User user = userRepository.findByUserId(userId);
        if (user != null){
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    // 유저 정보 가져오기 (UserDTO로 변경)
    public UserDTO getUser(String userId){
        User user = userRepository.findByUserId(userId);
        if (user == null){
            log.error("사용자를 찾을 수 없습니다. userId : {}", userId);
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return UserConverter.toDTO(user);
    }

    // 유저 저장 (UserDTO를 활용)
    public void saveUser(UserDTO userDTO) {
        User user = UserConverter.toDomain(userDTO);
        userRepository.save(user);
    }

}
