package com.project.FreeCycle.Controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.project.FreeCycle.Domain.Chat;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Dto.ChatDto;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.ChatService;
import com.project.FreeCycle.Service.Chat_ListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 리스트 불러오기 성공"),
            @ApiResponse(responseCode = "400", description = "채팅 리스트 불러오기 실패")
    })
    @Operation(summary = "채팅 리스트",
            description = "사용자가 가지고 있는 채팅방 리스트를 반환합니다." +
                    "채팅방을 클릭하면 /chat/room/{id}로 갑니다 이때 id는 채팅방 아이디 입니다."
    )
    @GetMapping(value = "/chat/main")
    public ResponseEntity<ApiResponseDTO<Map<String, List<ChatDto>>>> Main(Model model, Principal principal) {
        List<ChatDto> chatDtos = new ArrayList<>();
        for(Chat chat : userRepository.findByUserId(principal.getName()).getChats()){
            chatDtos.add(new ChatDto(chat.getId(), chat.getRoomId(), chat.getOtheruser()));
        }
        Map<String, List<ChatDto>> response = new HashMap<>();
        response.put("chatList", chatDtos);
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    //post에서 채팅방으로 들어감
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 들어가기 성공"),
            @ApiResponse(responseCode = "400", description = "채팅방 들어가기 실패")
    })
    @Operation(summary = "포스트에서 채팅방 들어가기",
            description = "사용자가 채팅 버튼을 클릭했을 때" +
                    "채팅방을 들어갑니다." +
                    "만약 채팅방이 없으면 만들어서 들어갑니다."
    )
    @PostMapping(value = "/post/chat/{id}")
    public ResponseEntity<ApiResponseDTO<Map<String, ChatDto>>> post(Model model, @PathVariable("id") Long user, Principal principal) {
        User this_user = userRepository.findById(user);
        Chat chat = chatService.findChat(userRepository.findById(user));
        Map<String, ChatDto> response = new HashMap<>();
        if(chat != null) {
            ChatDto chatDto = new ChatDto(chat.getId(), chat.getRoomId(), chat.getOtheruser());
            response.put("chat", chatDto);
            return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
        }else {
            Chat newchat = chatService.newChat(userRepository.findById(user), principal);
            ChatDto chatDto = new ChatDto(newchat.getId(), newchat.getRoomId(), newchat.getOtheruser());
            response.put("chat", chatDto);
            return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
        }
    }

    //챗 삭제
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 삭제하기 성공"),
            @ApiResponse(responseCode = "400", description = "채팅방 삭제하기 실패")
    })
    @Operation(summary = "채팅방 삭제하기",
            description = "삭제 버튼을 클릭했을 때" +
                    "채팅방을 삭제합니다."
    )
    @PostMapping(value = "/post/chat/delete/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(Model model, @PathVariable("id") long id, Principal principal) {
        List<Chat> chats = chatService.findRoomId(id);
        chatService.deleteChat(chats, principal);
        String response = "delete success";
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }
}