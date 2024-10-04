package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.LocationDTO;
import com.project.FreeCycle.Repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public void LocationSave(LocationDTO locationDTO, User user) {
        Location location = LocationConverter.toDomain(locationDTO);

        location.setUser(user);

        locationRepository.save(location);
    }

    // 삭제할 유저의 주소 정보 삭제 기능
    @Transactional
    public boolean deleteLocation(long user_id){
        try {
            locationRepository.deleteByUser_id(user_id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
