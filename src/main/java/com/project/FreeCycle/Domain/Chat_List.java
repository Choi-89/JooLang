package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Chat_List")
@Getter
@Setter
public class Chat_List {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String user_chat;

    @Column
    private String other_chat;

    @Column
    private String chat_time;

    @ManyToOne
    @JoinColumn(name ="Chat_id")
    private Chat chat;
}
