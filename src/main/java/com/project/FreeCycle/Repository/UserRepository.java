package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User save(User user);
    User findById(long id);
    User findByUserId(String userId);
    User findByName(String name);
    User findByNickname(String nickname);
    User findByLocation(String location);

}
