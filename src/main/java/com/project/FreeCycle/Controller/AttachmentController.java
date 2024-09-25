package com.project.FreeCycle.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class AttachmentController {

    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable("filename") String filename) throws IOException {
//        Path imagePath = Path.of("images", filename); // 이미지 파일 경로 설정
        Path imagePath = Path.of("/Users/leesangjin/project/images", filename); // 절대 경로로 변경
        byte[] imageBytes = Files.readAllBytes(imagePath);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png"); // 또는 "image/jpeg"

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
