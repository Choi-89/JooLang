package com.project.FreeCycle.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private String address;
    private long id;
    private String postcode;
    private String detailAddress;

    public LocationDTO(String address, String postcode, String detailAddress) {
        this.address = address;
        this.postcode = postcode;
        this.detailAddress = detailAddress;
    }


}
