package com.project.FreeCycle.Repository;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.Chat_List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Chat_ListRepository extends JpaRepository<Chat_List, Integer> {
    List<Chat_List> findAll(Chat chat);
}
