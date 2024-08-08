package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomOauth2UserServiceTest {

    @InjectMocks
    private CustomOauth2UserService customOauth2UserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private OAuth2User oAuth2User;

    private Map<String, Object> attributes;

    @BeforeEach
    public void setUp() {
        attributes = new HashMap<>();
        attributes.put("id", "12345");
        attributes.put("nickname", "testNickname");
    }

    @Test
    public void testLoadUser_NewUser() throws OAuth2AuthenticationException {
        // Mocking
        when(userRequest.getClientRegistration().getClientName()).thenReturn("Naver");
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(userRepository.findByUserId(anyString())).thenReturn(null); // 새로운 사용자

        // Execute
        OAuth2User result = customOauth2UserService.loadUser(userRequest);

        // Verify
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testLoadUser_ExistingUser() throws OAuth2AuthenticationException {
        // Mocking
        when(userRequest.getClientRegistration().getClientName()).thenReturn("Naver");
        when(oAuth2User.getAttributes()).thenReturn(attributes);
        when(userRepository.findByUserId(anyString())).thenReturn(new User()); // 기존 사용자

        // Execute
        OAuth2User result = customOauth2UserService.loadUser(userRequest);

        // Verify
        assertNotNull(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoadUser_UnsupportedProvider() {
        // Mocking
        when(userRequest.getClientRegistration().getClientName()).thenReturn("UnsupportedProvider");

        // Execute & Verify
        assertThrows(OAuth2AuthenticationException.class, () -> {
            customOauth2UserService.loadUser(userRequest);
        });
    }
}