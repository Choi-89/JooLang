package com.project.FreeCycle.Service;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.Product_Picture;
import com.project.FreeCycle.Repository.PictureRepository;
import com.project.FreeCycle.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PictureService {

    private final ProductRepository productRepository;
    private final PictureRepository pictureRepository;

    public void uploadPicture(Product product, List<MultipartFile> images){
        try{
            String uploadsDir = "src/resources/static/uploads/pictures/";

            for(MultipartFile image : images){
                String dbFilePath = saveImage(image, uploadsDir);

                Product_Picture picture = new Product_Picture(); //product, dbFilePath
                picture.setProduct(product);
                picture.setPicture_url(dbFilePath);
                pictureRepository.save(picture);
            }
        } catch(IOException e){
            e.printStackTrace();
        }



    }

    private String saveImage(MultipartFile image, String uploadsDir) throws IOException {

        //파일 이름
        String filename = UUID.randomUUID().toString().replace("-","") + "_" +image.getOriginalFilename();

        //파일이 실제 저장될 경로
        String filePath = uploadsDir + filename;

        //DB에 저장할 경로 문자열
        String dbFilePath = "/uploads/pictures/" + filename;

        Path path = Paths.get(filePath); //Path객체 생성
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, image.getBytes()); // 디렉토리에 파일 저장

        return dbFilePath;

    }

}
