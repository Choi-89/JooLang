package com.project.FreeCycle.Api;

import com.project.FreeCycle.Domain.AttachmentType;
import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.Product_Attachment;
import com.project.FreeCycle.Repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStoreApi {

    private final AttachmentRepository attachmentRepository;


    public FileStoreApi(AttachmentRepository attachmentRepository){
        this.attachmentRepository = attachmentRepository;
    }

    @Value("${file.dir}")
    private String fileDirPath;

    // 파일 삭제 메서드 추가
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete(); // 파일 삭제
        }
        return false; // 파일이 없으면 false 반환
    }

    public void deleteAttachments(List<Product_Attachment> attachments) {
        for (Product_Attachment attachment : attachments) {
            String viaPath = (attachment.getAttachmentType() == AttachmentType.IMAGE) ? "images/" : "generals/";
            String fullPath = fileDirPath + viaPath + attachment.getStoreFilename();
            deleteFile(fullPath);
        }
    }

    private String extractExt(String originalFilename){
        int idx = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(idx);
        return ext;
    }

    private String createStoreFilename(String originalFilename){
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        String storeFilename = uuid + ext;

        return storeFilename;
    }

    public String createPath(String storeFilename, AttachmentType attachmentType){
        String viaPath = (attachmentType == AttachmentType.IMAGE) ? "images/" : "generals/";
        return fileDirPath + viaPath + storeFilename;
    }

    public Product_Attachment storeFile(MultipartFile multipartFile, AttachmentType attachmentType) throws IOException{
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFilename(originalFilename);
        multipartFile.transferTo(new File(createPath(storeFilename, attachmentType)));

        return Product_Attachment.builder()
                .originFilename(originalFilename)
                .storePath(storeFilename)
                .attachmentType(attachmentType)
                .build();
    }

    public List<Product_Attachment> storeFiles(List<MultipartFile> multipartFiles, AttachmentType attachmentType) throws IOException{
        List<Product_Attachment> attachments = new ArrayList<>();
        if(multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (!multipartFile.isEmpty()) {
                    Product_Attachment attachment = storeFile(multipartFile, attachmentType);
                    attachments.add(attachment);
                }
            }

        }
        return attachments;
    }








}
