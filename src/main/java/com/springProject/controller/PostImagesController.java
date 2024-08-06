package com.springProject.controller;

import com.springProject.dto.PostImagesDto;
import com.springProject.service.PostImagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class PostImagesController {

    private final PostImagesService postImagesService;

    @PostMapping("/upload")
    //@PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<List<PostImagesDto>> uploadImages(@RequestPart List<MultipartFile> imageList, @RequestParam Long postId) throws IOException {
        List<PostImagesDto> postImagesDto = postImagesService.uploadImage(imageList, postId);
        return ResponseEntity.status(HttpStatus.OK).body(postImagesDto);
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
        return postImagesService.getImage(filename);
    }
}