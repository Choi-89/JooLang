package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_dibs")
@Getter
@Setter
public class Dibs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "dibs_id")
    private Long dibsId;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

}
