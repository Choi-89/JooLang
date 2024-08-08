package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<List<Chat>> findAll(User user){
        return Optional.ofNullable(chatRepository.findAll(user));
    }

    //자신의 채팅 리스트에 상대방과의 채팅이 있는지 확인
    public Optional<Chat> findOtheruser(User user){
        return Optional.ofNullable(chatRepository.findChat(user));
    }
}