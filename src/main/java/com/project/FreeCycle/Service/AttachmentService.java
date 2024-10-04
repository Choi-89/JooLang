package com.project.FreeCycle.Service;

import com.project.FreeCycle.Api.FileStoreApi;
import com.project.FreeCycle.Domain.AttachmentType;
import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.Product_Attachment;

import com.project.FreeCycle.Repository.AttachmentRepository;
import com.project.FreeCycle.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ProductRepository productRepository;
    private final FileStoreApi fileStoreApi;

    public List<Product_Attachment> saveAttachments(Map<AttachmentType, List<MultipartFile>> multipartFileListMap) throws IOException {
        List<Product_Attachment> imageFiles = fileStoreApi.storeFiles(multipartFileListMap
                .get(AttachmentType.IMAGE), AttachmentType.IMAGE);

        List<Product_Attachment> generalFiles = fileStoreApi.storeFiles(multipartFileListMap
                .get(AttachmentType.GENERAL), AttachmentType.GENERAL);

        List<Product_Attachment> result = Stream.of(imageFiles, generalFiles)
                .flatMap(f -> f.stream())
                .collect(Collectors.toList());

        return result;
    }

    public Map<AttachmentType, List<Product_Attachment>> findAttachments() {
        List<Product_Attachment> attachments = attachmentRepository.findAll();
        Map<AttachmentType, List<Product_Attachment>> result = attachments.stream()
                .collect(Collectors.groupingBy(Product_Attachment::getAttachmentType));

        return result;
    }

    public List<String> getPictures(Long productId){
        List<String> pictures = new ArrayList<>();

        Product product = productRepository.findById(productId).orElse(null);

        for(Product_Attachment tmp : product.getAttachments()){
            pictures.add(tmp.getStoreFilename());
        }

        return pictures;
    }

}














//    private final ProductRepository productRepository;
//    private final AttachmentRepository attachmentRepository;
//
//    public void uploadPicture(Product product, List<MultipartFile> images){
//        try{
//            String uploadsDir = "src/main/resources/static/uploads/pictures/";
//
//            for(MultipartFile image : images){
//                String dbFilePath = saveImage(image, uploadsDir);
//
////                Product_Attachment picture = new Product_Attachment(); //product, dbFilePath
//////                picture.setProduct(product);
//////                picture.setPicture_url(dbFilePath);
////                pictureRepository.save(picture);
//            }
//        } catch(IOException e){
//            e.printStackTrace();
//        }
//
//    }
//
//    private String saveImage(MultipartFile image, String uploadsDir) throws IOException {
//
//        //파일 이름
//        String filename = UUID.randomUUID().toString().replace("-","") + "_" +image.getOriginalFilename();
//
//        //파일이 실제 저장될 경로
//        String filePath = uploadsDir + filename;
//
//        //DB에 저장할 경로 문자열
//        String dbFilePath = "/uploads/pictures/" + filename;
////        String dbFilePath = "src/main/resources/static/uploads/pictures/" + filename;
//
//        Path path = Paths.get(filePath); //Path객체 생성
//        Files.createDirectories(path.getParent()); // 디렉토리 생성
//        Files.write(path, image.getBytes()); // 디렉토리에 파일 저장
//
//        return dbFilePath;
//
//    }
//
///*    public List<File> getPictures(Long productId){
//        List<File> pictures = new ArrayList<>();
//
//        Product product = productRepository.findById(productId).orElse(null);
//
//        for(Product_Picture tmp : product.getPictures()){
//            pictures.add(new File(tmp.getPicture_url()));
//        }
//
//        return pictures;
//    }
// */
//
//    public List<String> getPictures(Long productId){
//        List<String> pictures = new ArrayList<>();
//
//        Product product = productRepository.findById(productId).orElse(null);
//
//        for(Product_Attachment tmp : product.getPictures()){
////            pictures.add(tmp.getPicture_url());
//        }
//
//        return pictures;
//    }


