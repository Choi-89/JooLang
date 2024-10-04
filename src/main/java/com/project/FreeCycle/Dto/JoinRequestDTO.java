package com.project.FreeCycle.Dto;

import lombok.Getter;


public class JoinRequestDTO {
    private UserDTO userDTO;
    private LocationDTO locationDTO;

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public LocationDTO getLocationDTO() {
        return locationDTO;
    }
}
