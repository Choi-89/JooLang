package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.Chat_List;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.ChatRepository;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private long roomid = 0;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    //chat 생성
    public Chat newChat(User user, Principal principal) {
        Chat user_chat = new Chat();
        Chat other_chat = new Chat();
        User other_user = userRepository.findByUserId(principal.getName());
        user_chat.setUser(user);
        other_chat.setUser(other_user);
        user_chat.setRoomId(roomid);
        other_chat.setRoomId(roomid++);
        user_chat.setOtheruser(other_user.getNickname());
        other_chat.setOtheruser(user.getNickname());
        user.getChats().add(other_chat);
        other_user.getChats().add(user_chat);
        chatRepository.save(other_chat);
        chatRepository.save(user_chat);
        return user_chat;
    }

    public Chat findChat(User user){
        List<Chat> allChats = user.getChats();
        for (Chat chat : allChats) {
            System.out.println(chat.getUser().getNickname());
            if (chat.getUser().equals(user)) {
                return chat;
            }
        }
        return null;
    }

    public List<Chat> findRoomId(long id){
        return chatRepository.findAllByRoomId(id);
    }

    public void deleteChat(List<Chat> chat, Principal principal) {
        for (Chat chats : chat) {
            chatRepository.delete(chats);
        }
    }

    public List<Chat_List> findAllChat_List(long id){
        List<Chat> chats = chatRepository.findAllByRoomId(id);
        for(Chat chat : chats){
            if(!chat.getChat_lists().isEmpty()){
                return chat.getChat_lists();
            }
        }
        return null;
    }
}
