package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "userName")
    private String name;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "location")
    private int location;

    // 시큐리티 활용하여 admin, user 둘로 나눠서 저장 할 예정
    @Column(name = "role")
    private String role;




}
