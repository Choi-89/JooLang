package com.project.FreeCycle.Domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;


    @Column(name = "content")
    private String content;

    @Column(name = "view", nullable = false)
    private int view;

    @Column(name = "upload_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //Controller에서 @RequestParam 다음 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 어노테이션 사용
    private LocalDateTime upload_time;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;




//    @ManyToOne
//    @JoinColumn(name = "Product_Display_id", nullable = false)
//    private Product_Display product_display;
}