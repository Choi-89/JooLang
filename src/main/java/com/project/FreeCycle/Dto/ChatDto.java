package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Domain.Chat_List;
import com.project.FreeCycle.Domain.User;
import jakarta.persistence.*;

import java.util.List;

public class ChatDto {
    private Long id;
    private Long roomId;
    private String otheruser;

    public ChatDto(Long id, Long roomId, String otheruser) {
        this.id = id;
        this.roomId = roomId;
        this.otheruser = otheruser;
    }
}
