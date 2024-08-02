package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat save(User user) {
        Chat chat = new Chat();
        chat.setUser(user);
        chatRepository.save(chat);
        return chat;
    }

    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }

    public void findAll(User user){
        chatRepository.findAll(user);
    }
}
