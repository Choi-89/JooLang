package com.project.FreeCycle.Controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.FreeCycle.Domain.Chat;
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
    private final Chat_ListService chat_ListService;

    @Autowired
    public ChatController(ChatService chatService, UserRepository userRepository, Chat_ListService chatListService) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chat_ListService = chatListService;
    }
    //메인화면
    @GetMapping(value = "/chat/main")
    public String main(Model model, Principal principal) {
        List<Chat> chats =  userRepository.findByUserId(principal.getName()).getChats();
        for(Chat chat : chats) {
            System.out.println(chat.getOtheruser());
        }
        model.addAttribute("chat", userRepository.findByUserId(principal.getName()).getChats());
        return "chat";
    }

    //post에서 채팅방으로 들어감
    @PostMapping(value = "/post/chat")
    public String post(Model model, @RequestBody Long user, Principal principal) {
        Chat chat = chatService.findChat(userRepository.findById(user));
        if(chat != null) {
            long id = chat.getRoomId();
            System.out.println("/chat/room/" + id);
            return "redirect:/chat/room/"+id;
        }
        Chat newchat = chatService.newChat(userRepository.findById(user), principal);
        long id = newchat.getRoomId();
        return "redirect:/chat/room/"+id;
    }

    //챗 삭제
    @RequestMapping(value = "/post/chat/delete/{id}")
    public String delete(Model model, @PathVariable("id") long id, Principal principal) {
        List<Chat> chats = chatService.findRoomId(id);
        for(Chat chat : chats) {
            chatService.deleteChat(chat, principal);
        }
        return "redirect:/chat/main";
    }
}