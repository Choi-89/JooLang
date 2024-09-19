package com.project.FreeCycle.Domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

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

    //상품삭제 > 사진삭제 Cascade
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product_Attachment> attachments;

    @Builder
    public Product(long id, String name, String content, int view, LocalDateTime upload_time, List<Product_Attachment> attachments){
        this.id = id;
        this.name = name;
        this.content = content;
        this.view = view;
        this.upload_time = upload_time;
        this.attachments = attachments;
    }



//    @ManyToOne
//    @JoinColumn(name = "Product_Display_id", nullable = false)
//    private Product_Display product_display;
}