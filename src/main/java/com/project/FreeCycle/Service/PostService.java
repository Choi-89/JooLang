package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
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
        Product saveProduct = productRepository.save(product);

        //게시물 주인 지정
        product.setUser(userRepository.findByNickname(nickname));

        //해당User의 ProductList에 product추가
        User user = userRepository.findByNickname(nickname);
        user.getProducts().add(saveProduct);
        userRepository.save(user);
    }

    //글 수정
    public void postEdit(){}

    //글 삭제
    public ResponseEntity<String> postDelete(long id){
        if()


        return
    }

    //조회수 증가
    public void checkViews(Product product){
        product.setView(product.getView() + 1);
    }

    //게시글 목록 조회
    public Page<Product> getPosts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    //
    public User findUserId(String userid){
        return userRepository.findByUserId(userid);
    }

    public User findNickname(String nickname){
        return userRepository.findByNickname(nickname);
    }


}
