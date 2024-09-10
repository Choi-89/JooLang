package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.Chat_List;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.Chat_ListRepository;
import com.project.FreeCycle.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class Chat_ListService {
    private final Chat_ListRepository chat_ListRepository;
    private final ChatService chatService;
    private final UserRepository userRepository;

    @Autowired
    public Chat_ListService(Chat_ListRepository chatListRepository, ChatService chatService, UserRepository userRepository) {
        this.chat_ListRepository = chatListRepository;
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    //저장
    @Transactional
    public void saveChat(Map<String, Object> chat, Principal principal) {
        Chat_List chat_List = new Chat_List();
        long roomId = Long.parseLong(chat.get("sender").toString());
        List<Chat> chatting = chatService.findRoomId(roomId);
        chat_List.setChat_time(LocalDateTime.now());
        chat_List.setContent(chat.get("contents").toString());
        chat_List.setUsername(userRepository.findByUserId(principal.getName()).getNickname());
        chatting.get(0).getChat_lists().add(chat_List);
        //같은 아이디 가진것을 두곳에 모두 넣어주면 오류 생김 --> 한곳에만 넣어주고 값이 있는 chat_list만 불러오는 방식으로 바꿈
        chat_ListRepository.save(chat_List);
    }
}
