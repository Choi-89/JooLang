package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Dibs;
import com.project.FreeCycle.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//화이팅~
@Repository
public interface DibsRepository extends JpaRepository<Dibs, Integer> {
    List<Dibs> findAllByUser(User user);

    Optional<Dibs> findById(Long id);
}
