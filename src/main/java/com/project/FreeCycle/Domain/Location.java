package com.project.FreeCycle.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Location")
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // 우편번호
    @Column(name = "postcode")
    private String postcode;

    // 주소
    @Column(name = "address")
    private String address;

    // 상세 주소
    @Column(name = "detail_address")
    private String detailAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}
