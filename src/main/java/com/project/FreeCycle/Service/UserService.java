package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Dibs;
import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.LocationRepository;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;
    private LocationRepository locationRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public void userEdit(String userId, String nickname, Location location){
        User user = userRepository.findByUserId(userId);
        if(!nickname.isEmpty()){
            user.setNickname(nickname);
        }
        if(!(location.getAddress().isEmpty()
                || location.getDetailAddress().isEmpty()
                ||location.getPostcode().isEmpty()) )
        {
            location.setUser(user);
            locationRepository.save(location);
        }


    }


    public User getUser(String username){
        return userRepository.findByName(username);
    }


    public List<Product> getUserPosts(String userId){
        return userRepository.findByUserId(userId).getProducts();
    }

    public List<Product> getDibsPosts(String userId){
        return userRepository.findByUserId(userId).getDibs();
    }

//    public List<Product> getDibs(String userId){
//        return userRepository.findByUserId(userId).getDibs();
//    }
}
