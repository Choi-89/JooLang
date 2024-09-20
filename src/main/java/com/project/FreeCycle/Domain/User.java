package com.project.FreeCycle.Domain;

import com.project.FreeCycle.Util.AESUtil;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
    
@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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

    // 중복 회원가입 방지하기 위함
    @Column(name = "phoneNum")
    private String phoneNum;
    
    // 시큐리티 활용하여 admin, user 둘로 나눠서 저장 할 예정
    @Column(name = "role")
    private String role;

    // provider : 접속한 회사이름 들어감 ( 아직은 네이버 뿐)
    @Column(name = "provider")
    private String provider;

    // providerId : 네이버 로그인 한 유저의 고유 ID가 들어감
    @Column(name = "provider_id")
    private String providerId;

    // 유저가 가지고 있는 게시물
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    // 유저의 주소
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations = new ArrayList<>();

    //유저가 가지고있는 채팅
    @OneToMany(mappedBy="user", fetch=FetchType.LAZY)
    private List<Chat> chats = new ArrayList<>();

    public String getPhoneNum(){
        try{
            return AESUtil.decrypt(phoneNum);
        } catch (Exception e) {
            throw new RuntimeException("복호화 오류", e);
        }
    }

    public void setPhoneNum(String phoneNum){
        try{
            this.phoneNum = AESUtil.encrypt(phoneNum);
        } catch (Exception e) {
            throw new RuntimeException("암호화 오류", e);
        }
    }

}
