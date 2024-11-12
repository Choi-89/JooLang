package com.project.FreeCycle.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDTO {

    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력")
    private String password;

    @NotBlank(message = "비밀번호 확인 필수 입력")
    private String confirmPassword;
}

