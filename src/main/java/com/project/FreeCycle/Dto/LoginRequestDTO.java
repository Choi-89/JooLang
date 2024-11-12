package com.project.FreeCycle.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    
    @NotBlank(message = "사용자 ID 필수입력")
    private String userId;
    @NotBlank(message = "사용자 비밀번호 필수입력")
    private String password;
}
