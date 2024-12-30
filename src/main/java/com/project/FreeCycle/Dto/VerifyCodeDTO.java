package com.project.FreeCycle.Dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userId;

    private String email;

    private String type;

    /**
     * 이메일 인증 코드 혹은 휴대폰 인증 코드 둘다 사용
     * */
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;
}
