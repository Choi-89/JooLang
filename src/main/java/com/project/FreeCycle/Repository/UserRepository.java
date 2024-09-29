package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User save(User user);
    User findById(long id);
    User findByUserId(String userId);
    User findByNickname(String nickName);
    User findByEmail(String email);
    User findByPhoneNum(String phoneNum);
    boolean existsByUserId(String userId);

//    @Query("SELECT u FROM User u WHERE u.phoneNum = :phoneNum")
//    User findByPhone(@Param("phoneNum") String phoneNum); // 휴대폰 번호 검색 시 평문을 암호화해서 검색

}
