package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.Chat_List;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.ChatRepository;
import com.project.FreeCycle.Repository.Chat_ListRepository;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class Chat_ListService {
    private final Chat_ListRepository chat_ListRepository;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public Chat_ListService(Chat_ListRepository chatListRepository, ChatService chatService, UserRepository userRepository, ChatRepository chatRepository) {
        this.chat_ListRepository = chatListRepository;
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    //저장
    @Transactional
    public void saveChat(Map<String, Object> chat, Principal principal) {
        // 새로운 Chat_List 객체 생성
        Chat_List chatList = new Chat_List();
        long roomId = Long.parseLong(chat.get("sender").toString());

        // 해당 roomId에 해당하는 채팅방(Chat) 객체 찾기
        Chat foundChat = chatService.findRoomId(roomId).get(0);

        // Chat_List 필드 설정
        chatList.setChat_time(LocalDateTime.now());
        chatList.setContent(chat.get("contents").toString());
        chatList.setUsername(userRepository.findByUserId(principal.getName()).getNickname());

        // Chat 엔티티에 Chat_List 추가 (양방향 관계 설정)
        foundChat.addChatList(chatList);

        // 저장 (CascadeType.ALL로 인해 Chat_List도 자동으로 저장됨)
        chatRepository.save(foundChat); // 부모 저장 시 자식도 함께 저장됨
    }
}
