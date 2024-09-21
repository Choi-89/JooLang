package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.UserConverter;
import com.project.FreeCycle.Dto.UserDTO;
import com.project.FreeCycle.Repository.LocationRepository;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Util.HashUtil;
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
    @Transactional
    public User saveUser(UserDTO userDTO) {
        log.info("User 저장 시작: {}", userDTO);

        if (userRepository.existsByUserId(userDTO.getUserId())) {
            log.error("중복된 UserID: {}", userDTO.getUserId());
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        if (userDTO.getPhoneNum() != null && !userDTO.getPhoneNum().matches("^[a-fA-F0-9]{64}$")) {
            log.info("휴대폰 해시화 시작");
            userDTO.setPhoneNum(HashUtil.hashPhoneNumber(userDTO.getPhoneNum()));
            log.info("휴대폰 해시화 완료");
        }

        if (userDTO.getPassword() != null) {
            log.info("비밀번호 암호화 시작");
            userDTO.setPassword(passwordUtil.encodePassword(userDTO.getPassword())); // 비밀번호 인코딩
            log.info("비밀번호 암호화 완료 ");
        }

        if (userDTO.getRole() == null) {
            userDTO.setRole("ROLE_USER"); // 역할이 설정되지 않은 경우 기본값 설정
            log.info("User Role 저장 : {}", userDTO);
        }

        User user = UserConverter.toDomain(userDTO);
        log.info("User 변환 완료: {}", user);

        userRepository.save(user);
        log.info("User 저장 완료: {}", user);

        return userRepository.findByUserId(userDTO.getUserId());
    }

}
