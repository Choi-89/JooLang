package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "Chat_List")
@Getter
@Setter
public class Chat_List {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_chat", nullable = true)
    private String user_chat;

    @Column(name = "other_chat", nullable = true)
    private String other_chat;

    @CreatedDate
    @Column(name = "chat_time", nullable = false)
    private LocalDateTime chat_time;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    private Chat chat;
}
