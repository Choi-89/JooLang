package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Repository.ProductRepository;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PostService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(ProductRepository productRepository, UserRepository userRepository){
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    //글 작성
    public void postProduct(Product product, String nickname){

        //조회수 초기화
        product.setView(0);

        //글쓴이 작성 시간 저장, 닉네임 저장
        product.setUpload_time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        product.setName(nickname);

        //게시글 저장
        Product savedProduct = productRepository.save(product);

        product.setUser(userRepository.findBy(nickname));

    }

    //글 수정
    public void postEdit(){}

    //글 삭제
    public void postDelete(){}

    //조회수 증가
    public void checkViews(){}

    //게시글 목록 조회
    public Page<Product> getPosts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    //
    public String findUserId(String userid){
        return userRepository.findByUserId(userid);
    }

    public String findNickname(String nickname){
        return userRepository.findByNickname(nickname);
    }


}
