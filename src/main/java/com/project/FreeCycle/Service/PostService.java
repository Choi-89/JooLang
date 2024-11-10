package com.project.FreeCycle.Service;

import com.project.FreeCycle.Api.FileStoreApi;
import com.project.FreeCycle.Domain.*;

import com.project.FreeCycle.Dto.ProductDTO;
import com.project.FreeCycle.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.springframework.http.ResponseEntity.ok;

@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // 안녕
    private final DibsRepository dibsRepository;
    private final AttachmentRepository attachmentRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final FileStoreApi fileStoreApi;


    private final AttachmentService attachmentService;

    @Autowired
    public PostService(ProductRepository productRepository,
                       UserRepository userRepository,
                       DibsRepository dibsRepository,
                       AttachmentService attachmentService,
                       AttachmentRepository attachmentRepository,
                       CategoryRepository categoryRepository,
                       ProductCategoryRepository productCategoryRepository,
                       FileStoreApi fileStoreApi){
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.dibsRepository = dibsRepository;
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.fileStoreApi = fileStoreApi;
    }



    //글 작성
    public void postProduct(ProductDTO productDTO ,String userId) throws IOException {

        Product product = productDTO.createProduct();
        product.setUser(userRepository.findByUserId(userId));
        product = productRepository.save(product);


        //이미지 저장
        if(!productDTO.getAttachmentFiles().isEmpty()){
            //빈 attachment 넣어줌
            List<Product_Attachment> attachments = product.getAttachments();
            //이름 변경 후 다시 저장
            attachments.addAll(attachmentService.saveAttachments(productDTO.getAttachmentFiles()));
            //이름 바꾼 이미지 정보들 product에 set
            product.setAttachments(attachments);

            System.out.println(product.getAttachments().size());

            for(Product_Attachment attachment : attachments){
                attachment.setProduct(product);
//                attachmentRepository.save(attachment);
                log.info(attachment.getOriginFilename());
            }
            attachmentRepository.saveAll(attachments);


        }

        //카테고리 설정
        Category category = categoryRepository.findByCategory(productDTO.getCategory());

        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(product);
        productCategory.setCategory(category);
        productCategoryRepository.save(productCategory);


    }


    //게시글 목록 조회
    public Page<Product> getPosts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getAllProducts() {
//        return productRepository.findAll();
        List<Product> products = productRepository.findAll();
//        products.sort();
        return products;
    }

    //글 수정
    public void postEdit(long id , String name , String content , String category){
        Product product = productRepository.findById(id).get();
        if(!name.isEmpty()){
            product.setName(name);
        }
        product.setContent(content);

        productCategoryRepository.delete(productCategoryRepository.findByProduct_Id(id));
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(categoryRepository.findByCategory(category));
        productCategory.setProduct(productRepository.findById(id).orElse(null));
        productCategoryRepository.save(productCategory);

        productRepository.save(product);
    }

    //글 삭제
    public void postDelete(long id){
        Product product = productRepository.findById(id).get();
        productCategoryRepository.delete(productCategoryRepository.findByProduct_Id(product.getId()));
        // 첨부 파일 및 외부 파일 삭제
        List<Product_Attachment> attachments = product.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            fileStoreApi.deleteAttachments(attachments); // 파일 시스템에서 파일 삭제
        }
        productRepository.delete(product);



    }

    //조회수 증가
    public Product checkViews(Long id){
        Product product = productRepository.findById(id).get();
        product.setView(product.getView() + 1);
        productRepository.save(product);
        return product;
    }
    public Optional<Product> getProduct(long id){
        return productRepository.findById(id);
    }


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
            dibsRepository.save(newDibs);
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

    public List<Product> getProducts(String categoryname){
        System.out.println(categoryname);
        Category category = categoryRepository.findByCategory(categoryname);
        List<Product> products = new ArrayList<>();
        if(!categoryname.equals("전체")) {
            List<ProductCategory> productCategories = productCategoryRepository.findAllByCategory(category);
            for (ProductCategory tmp : productCategories) {
                products.add(tmp.getProduct());
            }
        }
        else{
            products =  productRepository.findAll();
        }
        return products;
    }







}