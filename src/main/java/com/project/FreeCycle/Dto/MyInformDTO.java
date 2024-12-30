package com.project.FreeCycle.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyInformDTO {

    private String nickname;
    private String postcode;
    private String address;
    private String detail_address;
    
    public MyInformDTO(String nickname, String postcode, String address, String detail_address) {
        this.nickname = nickname;
        this.postcode = postcode;
        this.address = address;
        this.detail_address = detail_address;
    }
}
