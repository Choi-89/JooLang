package com.project.FreeCycle.Dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeDTO {

    private String userId;

    private String email;

    private String type;

    /**
     * 이메일 인증 코드 혹은 휴대폰 인증 코드 둘다 사용
     * */
    private String code;

    private String phoneNumber;
}
