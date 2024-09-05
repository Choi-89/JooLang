package com.project.FreeCycle.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
public class ProductDTO {
    private String name; //제목
    private String content; //내용
    private List<MultipartFile> pictures;






}

