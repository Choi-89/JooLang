package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.ProductRepository;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

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

        //글쓴이 작성 시간 저장, (작성 시간 FORMATTER로 형식 변환 후 다시 LocalDateTime으로 타입 변환)
        String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        product.setUpload_time(LocalDateTime.parse(localDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) ;

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
    public void postEdit(long id , String name , String content){
        Product product = productRepository.findById(id).get();
        if(!name.isEmpty()){
            product.setName(name);
        } //비어있으면 기존 제목 다시 사용
        product.setContent(content);

        productRepository.save(product);
    }

    //글 삭제
    public ResponseEntity<String> postDelete(long id){
//        String redirectUrl = "redirect:/postlist";
        if(productRepository.findById(id).isPresent()){
            productRepository.delete(productRepository.findById(id).get());
//            return ResponseEntity.ok("<script>alert('삭제되었습니다.');"
//                    + "window.location.href='" + redirectUrl + "';"
//                    + "</script>");
            return ResponseEntity.ok("deleted successfully");
        }
        else{
//            return new ResponseEntity<>("<script>alert('이미 삭제된 게시글.');"
//                + "window.location.href='" + redirectUrl + "';"
//                + "</script>", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("delete fail");
        }
    }

    //조회수 증가
    public Product checkViews(Long id){
        Product product = productRepository.findById(id).get();
        product.setView(product.getView() + 1);
        return product;
    }

    //게시글 목록 조회
    public Page<Product> getPosts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    //
    public Optional<Product> getProduct(long id){
        return productRepository.findById(id);
    }


    public User findUserId(String userid){
        return userRepository.findByUserId(userid);
    }

    public User findNickname(String nickname){
        return userRepository.findByNickname(nickname);
    }

    public void saveDibs(String userId , Product product){
        User user = userRepository.findByUserId(userId);
        if(user.getDibs().contains(product)) user.getDibs().remove(product);
        else user.getDibs().add(product);
    }
}
