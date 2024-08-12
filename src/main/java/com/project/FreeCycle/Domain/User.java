package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "userId", nullable = false,unique = true)
    private String userId;

    @Column(name = "userName")
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "location")
    private int location;

    // 시큐리티 활용하여 admin, user 둘로 나눠서 저장 할 예정
    @Column(name = "role")
    private String role;

    //유저가 가지고 있는 게시물(마이페이지 기능 구현 유리)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Product> products;

    //찜목록
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Product> dibs; //dibs == 찜


}
