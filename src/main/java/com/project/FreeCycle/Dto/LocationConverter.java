package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Domain.Location;


public class LocationConverter {
    public static LocationDTO toDTO(Location location) {
        return new LocationDTO(
                location.getAddress(),
                location.getPostcode(),
                location.getDetailAddress()
        );
    }

    public static Location toDomain(LocationDTO locationDTO) {
        Location location = new Location();
        location.setAddress(locationDTO.getAddress());
        location.setPostcode(locationDTO.getPostcode());
        location.setDetailAddress(locationDTO.getDetailAddress());
        return location;
    }
}
