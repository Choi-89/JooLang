package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Domain.Dibs;
import com.project.FreeCycle.Repository.LocationRepository;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService{

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    // 비밀번호 체크
    public boolean checkPassword(String userId,String password){
        User user = userRepository.findByUserId(userId);

        if (user!=null && bCryptPasswordEncoder.matches(password,user.getPassword())){
            return true;
        }
        return false;
    }

    // 유저 삭제
    @Transactional
    public boolean deleteUser(String userId){
        User user = userRepository.findByUserId(userId);
        if (user != null){
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    // 유저 정보 수정

    public void userEdit(String userId, String nickname,
                         String postcode, String address, String detailAddress ){
        User user = userRepository.findByUserId(userId);
        Location location = user.getLocation();
//        location.setId(user.getLocation().getId());
        if(!nickname.isEmpty()){
            user.setNickname(nickname);
        }
        if(!(location.getAddress().isEmpty()
                || location.getDetailAddress().isEmpty()
                ||location.getPostcode().isEmpty()) )
        {
            location.setAddress(address);
            location.setDetailAddress(detailAddress);
            location.setPostcode(postcode);

            System.out.println(user.getUserId());
            System.out.println(user.getPassword());
        }
        userRepository.save(user);


    }


    public User getUser(String userId){
        return userRepository.findByUserId(userId);
    }


    public List<Product> getUserPosts(String userId){
        return userRepository.findByUserId(userId).getProducts();
    }



//    public List<Product> getDibs(String userId){
//        return userRepository.findByUserId(userId).getDibs();
//    }
}
