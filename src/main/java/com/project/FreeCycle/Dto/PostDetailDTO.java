package com.project.FreeCycle.Dto;


import com.project.FreeCycle.Domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDetailDTO {
    private Product product;
    private String nickname;
    private List<String> pictures;

    public PostDetailDTO(Product product, String nickname, List<String> pictures){
        this.product = product;
        this.nickname = nickname;
        this.pictures = pictures;
    }

}
