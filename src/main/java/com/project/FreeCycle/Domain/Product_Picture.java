package com.project.FreeCycle.Domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Product_Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "picture_url")
    private String picture_url;

    @ManyToOne
    @JoinColumn(name = "Product_id", nullable = false)
    private Product product;
}
