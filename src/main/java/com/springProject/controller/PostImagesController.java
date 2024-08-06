package com.springProject.controller;

import com.springProject.dto.PostImagesDto;
import com.springProject.entity.PostImages;
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
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class PostImagesController {

    private final PostImagesService postImagesService;

    @PostMapping("/upload/temp")
    public ResponseEntity<Map<Long, String>> uploadTemporaryImages(@RequestPart List<MultipartFile> imageList) throws IOException {
        Map<Long, String> imageIds = postImagesService.uploadTemporaryImages(imageList);
        return ResponseEntity.status(HttpStatus.OK).body(imageIds);
    }

    @PostMapping("/attachImages")
    public ResponseEntity<List<PostImagesDto>> attachImagesToPost(@RequestBody List<Long> imageIds, @RequestParam Long postId) {
        List<PostImagesDto> postImagesDto = postImagesService.attachImagesToPost(imageIds, postId);
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