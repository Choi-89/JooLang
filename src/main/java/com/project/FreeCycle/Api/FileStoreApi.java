package com.project.FreeCycle.Api;

import com.project.FreeCycle.Domain.AttachmentType;
import com.project.FreeCycle.Domain.Product_Attachment;
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

    @Value("${file.dir}/")
    private String fileDirPath;

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
        for(MultipartFile multipartFile : multipartFiles){
            if(!multipartFile.isEmpty()){
                attachments.add(storeFile(multipartFile,attachmentType));
            }
        }
        return attachments;
    }








}
