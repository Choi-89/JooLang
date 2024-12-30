package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Chat_List;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.ChatRepository;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.ChatService;
import com.project.FreeCycle.Service.Chat_ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.security.Principal;
import java.util.Map;

@Controller
public class Chat_ListController{
    private final SimpMessagingTemplate messagingTemplate;
    private final Chat_ListService chat_ListService;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private long chatroomid;

    @Autowired
    public Chat_ListController(SimpMessagingTemplate messagingTemplate, Chat_ListService chatListService, ChatService chatService, UserRepository userRepository, ChatRepository chatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chat_ListService = chatListService;
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @GetMapping(value = "/chat/room/{id}")
    public String chatMain(@PathVariable("id") long id, Model model, Principal principal) {
        chatroomid= id;
        User username = userRepository.findByUserId(principal.getName());
        model.addAttribute("chat", id);
        model.addAttribute("UserName", username.getNickname());
        model.addAttribute("chatLists", chatService.findAllChat_List(id));
        return "chat-room";
    }

    @MessageMapping(value = "/chat/room/{id}")
    public void sendMsg(@Payload Map<String, Object> data, Principal principal) {
        chat_ListService.saveChat(data, principal);
        messagingTemplate.convertAndSend("/topic/"+chatroomid, data);
    }
}
