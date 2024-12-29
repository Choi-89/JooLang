package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Chat_List;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Dto.Chat_ListDto;
import com.project.FreeCycle.Repository.ChatRepository;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.ChatService;
import com.project.FreeCycle.Service.Chat_ListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 들어가기 및 이전채팅 불러오기 성공"),
            @ApiResponse(responseCode = "400", description = "채팅방 들어가기 및 이전채팅 불러오기 실패")
    })
    @Operation(summary = "채팅방 들어가기 및 이전 채팅방 불러오기",
            description = "채팅방에 들어갈 때 이전 채팅을 모두 불러옵니다. Chat_List를 list형식으로 불러옵니다."
    )
    @GetMapping(value = "/chat/room/{id}")
    public ResponseEntity<ApiResponseDTO<Map<String, List<Chat_ListDto>>>> chatMain(@PathVariable("id") long id, Model model, Principal principal) {
        chatroomid= id;
        User username = userRepository.findByUserId(principal.getName());
        List<Chat_ListDto> chatListDtos = new ArrayList<>();
        for(Chat_List chatList : chatService.findAllChat_List(id)){
            chatListDtos.add(new Chat_ListDto(chatList.getId(), chatList.getContent(),
                                chatList.getUsername(), chatList.getChat_time()));
        }
        Map<String, List<Chat_ListDto>> response = new HashMap<>();
        response.put("chatList", chatListDtos);
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챗 보내기 성공"),
            @ApiResponse(responseCode = "400", description = "챗 보내기 실패")
    })
    @Operation(summary = "챗 보내기",
            description = "입력한 채팅을 보냅니다. Json형태로 이름, 컨탠츠, 현재 유저 이름을 받고 DB저장 후 보냅니다."
    )
    @MessageMapping(value = "/chat/room/{id}")
    public void sendMsg(@Payload Map<String, Object> data, Principal principal) {
        chat_ListService.saveChat(data, principal);
        messagingTemplate.convertAndSend("/topic/"+chatroomid, data);
    }
}
