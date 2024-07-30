package com.project.FreeCycle.Domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private long id;



}
