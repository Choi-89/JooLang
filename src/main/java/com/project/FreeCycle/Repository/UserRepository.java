package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User save(User user);
    String findByUserId(String userId);
    String findByName(String name);
    String findByNickname(String nickname);
    String findByLocation(String location);


}
