//package com.project.FreeCycle.Service;
//
//import com.project.FreeCycle.Domain.User;
//import com.project.FreeCycle.Dto.UserConverter;
//import com.project.FreeCycle.Dto.UserDTO;
//import com.project.FreeCycle.Repository.UserRepository;
//import com.project.FreeCycle.Util.PasswordUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Slf4j
//public class JoinService {
//    @Autowired
//    private UserRepository userRepository;
//
//    private final PasswordUtil passwordUtil = new PasswordUtil(new BCryptPasswordEncoder());
//
//    @Autowired
//    private UserService userService;
//
//    public User UserSave(UserDTO userDTO){
//
//        // 중복회원 검사 추가 해야 함.
//        if (userRepository.existsByUserId(userDTO.getUserId())) {
//            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
//        }
//
//        // 비밀번호가 포함된 경우 즉시 암호화
//        if (userDTO.getPassword() != null) {
//            userDTO.setPassword(passwordUtil.encodePassword(userDTO.getPassword())); // 비밀번호 인코딩
//        }
//
//        userDTO.setRole("ROLE_USER");
//
//        userService.saveUser(userDTO);
//
//        User savedUser = userRepository.findByUserId(userDTO.getUserId());
//        log.info("Saved user: {}", savedUser);  // 저장된 유저 확인
//
//        return savedUser;
////        return userRepository.findByUserId(userDTO.getUserId());
//    }
//}
