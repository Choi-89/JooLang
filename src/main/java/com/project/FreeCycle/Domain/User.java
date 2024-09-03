package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "userId",unique = true)
    private String userId;

    @Column(name = "userName")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    // 시큐리티 활용하여 admin, user 둘로 나눠서 저장 할 예정
    @Column(name = "role")
    private String role;

    // provider : 접속한 회사이름 들어감 ( 아직은 네이버 뿐)
    @Column(name = "provider")
    private String provider;

    // providerId : 네이버 로그인 한 유저의 고유 ID가 들어감
    @Column(name = "provider_id")
    private String providerId;

    @OneToOne(mappedBy = "user")
    private Location location;

    // 유저가 가지고 있는 게시물
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    //찜목록
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Dibs> dibs = new ArrayList<>();


}
