package com.project.FreeCycle.Domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Product_Display {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
