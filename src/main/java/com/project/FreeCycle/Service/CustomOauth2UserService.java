package com.project.FreeCycle.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.CustomUserDetail;
import com.project.FreeCycle.Dto.NaverUserDetails;
import com.project.FreeCycle.Repository.OAuth2UserInfo;
import com.project.FreeCycle.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @PostConstruct
    public void init() {
        log.info("CustomOauth2UserService 빈 등록 완료");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("loadUser 메서드 호출됨");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            log.info("OAuth2User Attributes: {}", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        String provider = userRequest.getClientRegistration().getClientName();

        try{
            System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        OAuth2UserInfo oAuth2UserInfo;

        if(provider.equals("Naver")){
            log.info("네이버 로그인");
            oAuth2UserInfo = new NaverUserDetails(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원되지 않는 로그인 제공자입니다.");
        }


        String providerId = oAuth2UserInfo.getProviderId();
        String userId = provider + "_" + providerId;
        String name = oAuth2UserInfo.getName();
        String nickname = ((NaverUserDetails) oAuth2UserInfo).getNickname();


        // Optional을 사용하여 null 처리를 안전하게 함
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserId(userId));
        User user;

        if (userOptional.isEmpty()) {
            // 새로운 사용자 저장
            user = new User();
            user.setUserId(userId);
            user.setName(name); // 실제 이름 설정
            user.setPassword(null); // 소셜 로그인 사용자는 비밀번호를 사용하지 않음으로 null로 설정
            user.setRole("USER"); // 기본 역할 설정
            user.setNickname(nickname); // 닉네임 설정, 중복된 이름 설정 문제 해결
            user.setProvider(provider);
            userRepository.save(user);
            log.info("새로운 사용자 저장: {}", userId);
        } else {
            user = userOptional.get();
            log.info("기존 사용자 로그인: {}", userId);
        }

        return new CustomUserDetail(user, oAuth2User.getAttributes());
    }
}
