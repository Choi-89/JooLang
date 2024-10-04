package com.project.FreeCycle.Controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.ChatService;
import com.project.FreeCycle.Service.Chat_ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@JsonAutoDetect
public class ChatController {
    private final ChatService chatService;
    private final UserRepository userRepository;

    @Autowired
    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    //메인화면
    @GetMapping(value = "/chat/main")
    public String main(Model model, Principal principal) {
        model.addAttribute("chat", userRepository.findByUserId(principal.getName()).getChats());
        return "chat";
    }

    //post에서 채팅방으로 들어감
    @RequestMapping(value = "/post/chat/{id}")
    public String post(Model model, @PathVariable("id") Long user, Principal principal) {
        User this_user = userRepository.findById(user);
        Chat chat = chatService.findChat(userRepository.findById(user));
        if(chat != null) {
            model.addAttribute("chat", chat.getRoomId());
            model.addAttribute("UserName", this_user.getNickname());
            model.addAttribute("chatLists", chatService.findAllChat_List(chat.getRoomId()));
            System.out.println(chatService.findAllChat_List(chat.getRoomId()).size());
            return "redirect:/chat/room/"+chat.getRoomId();
        }else {
            Chat newchat = chatService.newChat(userRepository.findById(user), principal);
            return "redirect:/chat/room/"+newchat.getRoomId();
        }
    }

    //챗 삭제
    @RequestMapping(value = "/post/chat/delete/{id}")
    public String delete(Model model, @PathVariable("id") long id, Principal principal) {
        List<Chat> chats = chatService.findRoomId(id);
        chatService.deleteChat(chats, principal);
        return "redirect:/chat/main";
    }
}