package com.springProject.service;

import com.springProject.dto.PostImagesDto;
import com.springProject.entity.PostImages;
import com.springProject.entity.Posts;
import com.springProject.repository.PostImagesRepository;
import com.springProject.repository.PostsRepository;
import com.springProject.utils.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PostImagesService {

    private final PostImagesRepository postImagesRepository;
    private final PostsRepository postsRepository;

    @Value("${com.ex.uploadPath}")
    private String uploadPath;

    public Map<Long, String> uploadTemporaryImages(List<MultipartFile> imageList) throws IOException {
        Map<Long, String> imageInfoMap = new HashMap<>();

        for (MultipartFile multipartFile : imageList) {
            // 저장할 절대 경로
            String realPath = uploadPath;

            File file = new File(realPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            // 확장자 조회
            String extension = "";
            String contentType = multipartFile.getContentType();
            if (contentType.contains("image/jpeg")) {
                extension = ".jpg";
            } else if (contentType.contains("image/png")) {
                extension = ".png";
            } else if (contentType.contains("image/gif")) {
                extension = ".gif";
            }

            // 랜덤 파일명
            String storedFileName = UUID.randomUUID().toString() + extension;
            PostImages newImage = PostImages.builder()
                    .storeFilename(storedFileName)
                    .filePath(realPath)
                    .originFilename(multipartFile.getOriginalFilename())
                    .createdAt(LocalDateTime.now())
                    .posts(null)  // 아직 게시글과 연결되지 않음
                    .build();

            PostImages savedImage = postImagesRepository.save(newImage);

            // ID와 저장된 파일명으로 맵에 저장
            imageInfoMap.put(savedImage.getId(), storedFileName);

            // 절대 경로 + 파일명으로 파일 저장
            file = new File(realPath + "/" + storedFileName);
            multipartFile.transferTo(file);
        }

        return imageInfoMap;  // 임시로 저장된 이미지 ID와 파일명을 반환
    }

    public List<PostImagesDto> attachImagesToPost(List<Long> imageIds, Long postId) {
        Posts findPost = postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        List<PostImages> postImages = new ArrayList<>();

        for (Long imageId : imageIds) {
            PostImages image = postImagesRepository.findById(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 이미지 ID 입니다."));
            image.setPosts(findPost);  // 게시글과 연결
            postImages.add(image);
        }

        postImagesRepository.saveAll(postImages);
        findPost.getPostImages().addAll(postImages);

        return postImages.stream().map(ConvertUtils::convertImagesToDto).toList();
    }

    public void deleteImage(Long imageId) {
        postImagesRepository.deleteById(imageId);
    }

    public List<PostImagesDto> findAllById(Long postId) {
        return Optional.ofNullable(postImagesRepository.findAllById(postId)).orElseGet(Collections::emptyList)
                .stream().map(ConvertUtils::convertImagesToDto).toList();
    }

    public ResponseEntity<byte[]> getImage(String filename) {
        ResponseEntity<byte[]> result;
        try {
            String srcFileName = URLDecoder.decode(filename, StandardCharsets.UTF_8);
            File file = new File(uploadPath + File.separator + srcFileName);
            HttpHeaders header = new HttpHeaders();

            // MIME 타입 처리
            header.add("Content-Type", Files.probeContentType(file.toPath()));

            // 파일 데이터 처리
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}