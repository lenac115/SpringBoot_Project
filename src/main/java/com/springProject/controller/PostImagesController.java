package com.springProject.controller;

import com.springProject.dto.PostImagesDto;
import com.springProject.service.PostImagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class PostImagesController {

    private final PostImagesService postImagesService;

   /* @Value("${com.ex.uploadPath}")*/
    private String uploadPath;

    @PostMapping("/upload")
    //@PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<String> uploadImages(@RequestPart List<MultipartFile> imageList, @RequestParam Long postId) throws IOException {
        postImagesService.uploadImage(imageList, postId);
        return ResponseEntity.status(HttpStatus.OK).body("업로드 완료");
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<String> deleteImages(@RequestParam Long imageId) {
        postImagesService.deleteImage(imageId);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }

    @GetMapping("/get")
    public ResponseEntity<List<PostImagesDto>> getImages(@RequestParam Long postId) {
        List<PostImagesDto> postImagesDtoList = postImagesService.findAllById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(postImagesDtoList);
    }

    @GetMapping("/display/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        ResponseEntity<byte[]> result;
        try {
            String srcFileName = URLDecoder.decode(filename, StandardCharsets.UTF_8);
            File file = new File( uploadPath + File.separator + srcFileName);
            HttpHeaders header = new HttpHeaders();

            // MIME 타입 처리
            header.add("Content-Type", Files.probeContentType(file.toPath()));

            // 파일 데이터 처리
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}