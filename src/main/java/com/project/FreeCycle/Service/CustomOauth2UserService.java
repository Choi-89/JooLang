package com.project.FreeCycle.Service;


import com.project.FreeCycle.Dto.*;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Util.HashUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;



import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final VerifyService verifyService;
    private final LocationService locationService;
    private final HttpSession httpSession;
    private final HttpServletResponse httpServletResponse;
    private final UserService userService;

    @PostConstruct
    public void init() {
        log.info("CustomOauth2UserService 빈 등록 완료");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("loadUser 메서드 호출됨");
        OAuth2User oAuth2User = super.loadUser(userRequest); //회원 프로필 조회

        log.info("userRequest clientRegistration : {}", userRequest.getClientRegistration());
        log.info("oAuth2User : {}", oAuth2User);

        return processOAuth2User(userRequest, oAuth2User);

    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        String provider = userRequest.getClientRegistration().getClientName();
        OAuth2UserInfo oAuth2UserInfo = null;

        if(provider.equals("naver")){
            log.info("네이버 로그인");
            oAuth2UserInfo = new NaverUserDetails(oAuth2User.getAttributes());
        }


        String providerId = oAuth2UserInfo.getProviderId();
        String userId = oAuth2UserInfo.getEmail(); // userId에 email을 저장함으로써 향후 비밀번호 찾을 때 이메일을 사용하게 함
        String name = oAuth2UserInfo.getName();
        String nickname = oAuth2UserInfo.getNickname();
        String role = "ROLE_USER";
        String email = oAuth2UserInfo.getEmail();
        String phoneNum = oAuth2UserInfo.getMobile();
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserId(userId));
        
        // 하이폰 삭제하여 데베에 저장
        String cleanPhoneNum = phoneNum.replaceAll("-","");
        log.info(">>>데베에 저장 할 핸드폰 번호 : " + cleanPhoneNum);



        User user;
        /* 만약 처음 로그인 시도 했으면 회원가입이 비밀번호 세팅이 
        * 필요하므로 관련된 로직으로 수정 */
        if (userOptional.isEmpty()) {

            // 핸드폰 번호 중복 확인
            UserDTO result = verifyService.verifyPhoneNum(cleanPhoneNum);
            if (result != null) {
                log.error("이미 존재하는 핸드폰 번호로 회원가입 시도: {}", cleanPhoneNum);
                throw new OAuth2AuthenticationException("이미 가입된 연락처가 존재합니다.");
            }

            UserDTO userDTO = new UserDTO(userId, name, nickname, email, role, provider, providerId, cleanPhoneNum, 0);

            // 번호 해싱화하여 저장
            try{
//                String encryptedPhoneNum = AESUtil.encrypt(cleanPhoneNum);
                String encryptedPhoneNum = HashUtil.hashPhoneNumber(cleanPhoneNum);
                userDTO.setPhoneNum(encryptedPhoneNum);
            } catch (Exception e) {
                log.error("휴대폰 번호 해싱화 중 오류 발생",e);
            }

            User savedUser = userService.saveUser(userDTO);

            LocationDTO locationDTO = new LocationDTO(null,null,null);
            locationService.LocationSave(locationDTO,savedUser);

            log.info("새로운 사용자 저장: {}", userId);


            // 세션에 사용자 ID 저장
            httpSession.setAttribute("userId", userId);
            try {
                httpServletResponse.sendRedirect("/joinPassword"); // 클라이언트 개발자가 리다이렉트 할 URI
            } catch (IOException e) {
                log.error("리다이렉션 실패");
            }
            return new CustomUserDetail(savedUser, oAuth2User.getAttributes());
        } else {
            user = userOptional.get();
            log.info("기존 사용자 로그인: {}", userId);
        }
        return new CustomUserDetail(user, oAuth2User.getAttributes());
    }
}