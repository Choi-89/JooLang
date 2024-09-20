package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findById(long id);
    List<Chat> findAllByRoomId(long id);
}
