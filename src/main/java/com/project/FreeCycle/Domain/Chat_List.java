package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Chat_List {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private String username;

    @Column(nullable = false)
    private LocalDateTime chat_time;

    @ManyToOne
    private Chat chat;
}
