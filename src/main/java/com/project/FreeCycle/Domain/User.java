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

    // 우편번호
    @Column(name = "postcode")
    private String postcode;

    // 주소
    @Column(name = "address")
    private String address;

    // 상세 주소
    @Column(name = "detail_address")
    private String detailAddress;
}
