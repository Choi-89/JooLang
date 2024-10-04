package com.project.FreeCycle.Dto;

import com.project.FreeCycle.Domain.AttachmentType;
import com.project.FreeCycle.Domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Getter
@Setter
@NoArgsConstructor
public class ProductFormDTO {

    private static final Logger log = LoggerFactory.getLogger(ProductFormDTO.class);
    //not blank
    private String name;
    //not blank
    private String content;

    private List<MultipartFile> imageFiles;
    private List<MultipartFile> generalFiles;

    @Builder
    public ProductFormDTO(String name, String content, List<MultipartFile> imageFiles , List<MultipartFile> generalFiles){
        this.name = name;
        this.content = content;
        this.imageFiles = (imageFiles != null) ? imageFiles : new ArrayList<>();
        this.generalFiles = (generalFiles != null) ? generalFiles : new ArrayList<>();

    }

    public ProductDTO createProductDTO(){
        Map<AttachmentType, List<MultipartFile>> attachments = getAttachmentTypeListMap();

        return ProductDTO.builder()
                .name(name)
                .content(content)
                .attachmentFiles(attachments)
                .build();
    }


    private Map<AttachmentType, List<MultipartFile>> getAttachmentTypeListMap(){
        Map<AttachmentType, List<MultipartFile>> attachments = new ConcurrentHashMap<>();
        if (imageFiles != null) {
            attachments.put(AttachmentType.IMAGE, imageFiles);
        } else {
            // 이미지 파일 리스트가 null일 때 로그나 처리 로직 추가
            log.warn("imageFiles is null");
        }
        if (generalFiles != null) {
            attachments.put(AttachmentType.GENERAL, generalFiles);
        } else {
            // 일반 파일 리스트가 null일 때 로그나 처리 로직 추가
            log.warn("generalFiles is null");
        }
        return attachments;
    }


}
