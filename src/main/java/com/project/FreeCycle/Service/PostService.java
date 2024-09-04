package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Dibs;
import com.project.FreeCycle.Domain.Product;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.DibsRepository;
import com.project.FreeCycle.Repository.ProductRepository;
import com.project.FreeCycle.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@Service
public class PostService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository; // 안녕
    private final DibsRepository dibsRepository;

    @Autowired
    public PostService(ProductRepository productRepository,
                       UserRepository userRepository,
                       DibsRepository dibsRepository){
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.dibsRepository = dibsRepository;
    }

    //글 작성
    public void postProduct(Product product, String nickname){

        //조회수 초기화
        product.setView(0);

        //글쓴이 작성 시간 저장, 닉네임 저장, (작성 시간 FORMATTER로 형식 변환 후 다시 LocalDateTime으로 타입 변환)
        String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        product.setUpload_time(LocalDateTime.parse(localDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) ;
//
//        product.setName(nickname);

//        User user = userRepository.findByNickname(nickname);
//        //게시물 주인 지정
//        product.setUser(user);

        //게시글 저장
        Product saveProduct = productRepository.save(product);

        User user = product.getUser();
        //해당User의 ProductList에 product추가
        user.getProducts().add(saveProduct);
        userRepository.save(user);
    }

    //게시글 목록 조회
    public Page<Product> getPosts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    // 지선생

    //글 수정
    public void postEdit(long id , String name , String content){
        Product product = productRepository.findById(id).get();
        if(!name.isEmpty()){
            product.setName(name);
        }
        product.setContent(content);

        productRepository.save(product);
    }

    //글 삭제
    public void postDelete(long id){
//        String redirectUrl = "/게시글목록";
//        if(productRepository.findById(id).isPresent()){
            productRepository.delete(productRepository.findById(id).get());
//        }
//        else{
//            return new ResponseEntity<>("<script>alert('이미 삭제된 게시글.');"
//                    + "window.location.href='" + redirectUrl + "';"
//                    + "</script>", HttpStatus.NOT_FOUND);
//        }
    }

    //조회수 증가
    public Product checkViews(Long id){
        Product product = productRepository.findById(id).get();
        product.setView(product.getView() + 1);
        productRepository.save(product);
        return product;
    }

//    //
    public Optional<Product> getProduct(long id){
        return productRepository.findById(id);
    }
//
//
//    public User findUserId(String userid){
//        return userRepository.findByUserId(userid);
//    }
//
//    public User findNickname(String nickname){
//        return userRepository.findByNickname(nickname);
//    }


//    public void saveDibs(String userId , long postId){
//        //유저의 찜 목록 불러오기
//        User user = userRepository.findByUserId(userId);
//        // user 정보 따로 저장
//        List<Product> userDibs = user.getDibs(); //도메인 dibs 추가 전
//        System.out.println(userDibs);
//
//        for(int i = 0; i < userDibs.size(); i++){
//            System.out.println(userDibs.get(i).getId());
//        }
////        Dibs dibs = user.getMyDibs(); //도메인 dibs 추가 후
////        List<Product> userDibs = dibs.getDibs();
//        //해당 글
//        Product product = productRepository.findById(postId).orElse(null);
//
//        if(userDibs.contains(product)){
//            userDibs.remove(product);
//            System.out.println("찜 삭제");
//        }
//        else {
//            userDibs.add(product);
//            System.out.println("찜 등록");
//        }
//
//        product.setView(product.getView() - 1);
//
//        //유저정보 갱신
//        userRepository.findByUserId(userId).setDibs(userDibs);
//        userRepository.save(user);
//
//
//    }

    public void saveDibs(String userId, long postId){

        User user = userRepository.findByUserId(userId);

        List<Dibs> dibs = user.getDibs();
        Product product = productRepository.findById(postId).orElse(null);
        product.setView(product.getView() - 1);

        //게시물이 User에 있는지 == isThat
        boolean isThat = true;
        int i = 0;
        for(Dibs dib: dibs){
            if(dib.getDibsId().equals(postId)){
                dibs.remove(dib);
                dibsRepository.delete(dib);
                return;
            }
        }

        //찜한거 존재 x
        if(isThat){
            Dibs newDibs = new Dibs();
            newDibs.setDibsId(postId);
            newDibs.setUser(user);
            dibs.add(newDibs);
        }



//        user.setDibs(dibs);
    }
//화이팅~
    public List<Product> getDibsPosts(String userId){

        User user = userRepository.findByUserId(userId);
//        List<Dibs> dibs = userRepository.findByUserId(userId).getDibs();
        List<Dibs> dibs = dibsRepository.findAllByUser(user);

        List<Product> products = new ArrayList<>();
        for(Dibs tmp : dibs ){
            if(tmp != null) {
                products.add(productRepository.findById(tmp.getDibsId()).orElse(null));
            }
        }
        return products;
    }

}