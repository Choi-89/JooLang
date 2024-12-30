package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(Long user);
    User save(User user);
    User findByUserId(String userId);
    User findByEmail(String email);
    User findByPhoneNum(String phoneNum);
    boolean existsByUserId(String userId);
}
