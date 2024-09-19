package com.project.FreeCycle.Domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product_Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String originFilename;
    private String storeFilename;

    @Enumerated(EnumType.STRING)
    private AttachmentType attachmentType;


    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public Product_Attachment(long id, String originFilename, String storePath, AttachmentType attachmentType){
        this.id = id;
        this.originFilename = originFilename;
        this.storeFilename = storePath;
        this.attachmentType = attachmentType;
    }



/*
    @ManyToOne
    @JoinColumn(name = "product_display_id", nullable = false)
    private Product_Display product_display;
*/


}
