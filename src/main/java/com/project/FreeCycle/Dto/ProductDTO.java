package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Domain.AttachmentType;
import com.project.FreeCycle.Domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Getter
@Setter
public class ProductDTO {
    private String name; //제목
    private String content; //내용

    private Map<AttachmentType, List<MultipartFile>> attachmentFiles = new ConcurrentHashMap<>();

    @Builder
    public ProductDTO(String name , String content
            , Map<AttachmentType,List<MultipartFile>> attachmentFiles){
        this.name = name;
        this.content = content;
        this.attachmentFiles = attachmentFiles;
    }

    public Product createProduct(){
        return Product.builder()
                .name(name)
                .content(content)
                .attachments(new ArrayList<>())
                .upload_time(LocalDateTime.now())
                .view(0)
                .build();
    }





}

