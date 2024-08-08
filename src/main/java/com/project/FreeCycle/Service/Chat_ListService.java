package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.Chat_List;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.Chat_ListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class Chat_ListService {
    private final Chat_ListRepository chatListRepository;
    private User chatUser;

    @Autowired
    public Chat_ListService(Chat_ListRepository chatListRepository) {
        this.chatListRepository = chatListRepository;
    }

    public void addChat(User user, String content) {
        Chat_List chat = new Chat_List();
        if(user == chatUser) chat.setUser_chat(user.getName());
        else chat.setOther_chat(user.getName());
        chat.setChat_time(LocalDateTime.now());
        chat.setContent(content);
        chatListRepository.save(chat);
    }

    public void deleteChat(Chat_List chat) {
        chatListRepository.delete(chat);
    }

    public List<Chat_List> findChat(Chat chat){
        return chatListRepository.findAll(chat);
    }
}
