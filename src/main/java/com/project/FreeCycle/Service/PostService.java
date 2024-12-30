package com.project.FreeCycle.Service;

import com.project.FreeCycle.Api.FileStoreApi;
import com.project.FreeCycle.Domain.*;

import com.project.FreeCycle.Dto.ProductDTO;
import com.project.FreeCycle.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;


import static com.project.FreeCycle.Domain.AttachmentType.IMAGE;

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

            for(Product_Attachment attachment : attachments){
                attachment.setProduct(product);
                log.info(attachment.getOriginFilename());
            }
            attachmentRepository.saveAll(attachments);


        }

        //찜수 0으로 설정
        product.setDibsCount(0);

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
    public void postEdit(long productid, ProductDTO productDTO) throws IOException {

        Product product = productRepository.findById(productid).get();

        //이미지 가져와서 파일 저장하는 로직
        //원래 이미지, 수정한 이미지 비교후 바뀐게 있으면 업데이트해서 저장
        List<Product_Attachment> stringOriginal = attachmentRepository.findAllByProduct_Id(productid);
        List<String> originalFilename = new ArrayList<>();
        for (Product_Attachment imageFile : stringOriginal) {
            originalFilename.add(imageFile.getOriginFilename());
        }

        //if ? 1 : 0 나중에 바꿀것 얘는 멀파파 스트링이 아님
        List<String> insertFilenames = new ArrayList<>();
        List<MultipartFile> imageFiles = productDTO.getAttachmentFiles().get(IMAGE);
        if(imageFiles != null) {
            for (MultipartFile imageFile : imageFiles) {
                insertFilenames.add(Normalizer.normalize(imageFile.getOriginalFilename(), Normalizer.Form.NFC));
            }
        }
        // 원래 있던 프로가 없으면 데베에서 삭제

        List<Product_Attachment> mustBeDeletedFiles = new ArrayList<>();
        for (String name : originalFilename) {
            List<Product_Attachment> product_original = new ArrayList<>();
            if (!insertFilenames.contains(name)) {
                product_original.addAll(attachmentRepository.findByOriginFilename(name));
                for(Product_Attachment attachment : product_original){
                    if(productid == attachment.getProduct().getId()) {
                        mustBeDeletedFiles.add(attachment);
                        product.getAttachments().remove(attachment);
                    }
                }

            }
        }
        fileStoreApi.deleteAttachments(mustBeDeletedFiles);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            Map<AttachmentType, List<MultipartFile>> fileMap = new HashMap<>();
            for (MultipartFile file : imageFiles) {
                String fileName = Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC);
                // 원래 있던 프로덕트가 아니면 추가
                if (!originalFilename.contains(fileName)) {
                    AttachmentType type = attachmentService.determineAttachmentType(file);
                    fileMap.computeIfAbsent(type, k -> new ArrayList<>()).add(file); // 유형별로 파일 추가
                }
            }
            product.getAttachments().addAll(attachmentService.saveAttachments(productid, fileMap));
        }


        //이름, 내용, 카테고리 수정하는 로직
//        Product product = productRepository.findById(productid).get();
        if(!productDTO.getName().isEmpty()){
            product.setName(productDTO.getName());
        }
        product.setContent(productDTO.getContent());
        productCategoryRepository.delete(productCategoryRepository.findByProduct_Id(productid));
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(categoryRepository.findByCategory(productDTO.getCategory()));
        productCategory.setProduct(productRepository.findById(productid).orElse(null));
        productCategoryRepository.save(productCategory);

        productRepository.save(product);

        //수정을 눌렀을때 파일 선택하는 칸에 이미 이미지가 존재하는 경우에 선택이 가능하도록
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
                isThat = false;
                dibsRepository.delete(dib);
                product.setDibsCount(product.getDibsCount()-1);
                break;
            }
        }

        //찜한거 존재 x
        if(isThat){
            Dibs newDibs = new Dibs();
            newDibs.setDibsId(postId);
            newDibs.setUser(user);
            dibs.add(newDibs);
            product.setDibsCount(product.getDibsCount()+1);
            dibsRepository.save(newDibs);
        }
        productRepository.save(product);


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

    public List<Product> getProducts(String categoryname, String sort){
        Category category = categoryRepository.findByCategory(categoryname); // 카테고리id, postid >> 프로덕트카테고리 , 그냥 카테고리는 id, 카테고리이름
        List<Product> products = new ArrayList<>();
        if(sort.equals("latest")){
            products = productRepository.findAllByOrderByUploadTimeDesc();
        }
        else{
            products = productRepository.findAllByOrderByDibsCountDesc();
        }

        if(!categoryname.equals("전체")) {
            List<ProductCategory> categoryProducts = productCategoryRepository.findAllByCategory(category);
            List<Long> postIds = categoryProducts.stream()
                    .map(productCategory -> productCategory.getProduct().getId())
                    .collect(Collectors.toList()); // 리스트로 변환

            List<Product> result = new ArrayList<>();
            for (Product post : products) {
                if (postIds.contains(post.getId())) {
                    result.add(post);
                }
            }
            return result;
        }
        return products;
    }







}