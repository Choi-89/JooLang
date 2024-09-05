package com.project.FreeCycle.Domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;

@Entity
@Table
@Getter
@Setter
public class Product_Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "picture_url",nullable = false)
    private String picture_url;


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

//    @ManyToOne
//    @JoinColumn(name = "product_display_id", nullable = false)
//    private Product_Display product_display;




}
