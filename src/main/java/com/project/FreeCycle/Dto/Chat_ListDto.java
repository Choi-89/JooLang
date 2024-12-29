package com.project.FreeCycle.Dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class Chat_ListDto {
    private Long id;
    private String content;
    private String username;
    private LocalDateTime chat_time;

    public Chat_ListDto(Long id, String content, String username, LocalDateTime chat_time) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.chat_time = chat_time;
    }
}
