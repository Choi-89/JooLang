package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {
    User save(User user);
    User findByUserId(String userId);

}
