package com.springProject.controller;

import com.springProject.dto.PostsDto;
import com.springProject.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private final PostsService postsService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    //생성
    @PostMapping
    public ResponseEntity<PostsDto> createPost(@RequestBody PostsDto postsDto) {
        PostsDto createdPostDto = postsService.createPost(postsDto);
        return ResponseEntity.ok(createdPostDto);
    }

    //조회
    @GetMapping
    public ResponseEntity<List<PostsDto>> getAllPosts() {
        List<PostsDto> postsDto = postsService.getAllPosts();
        return ResponseEntity.ok(postsDto);
    }

    //상세조회
    @GetMapping("/{id}")
    public ResponseEntity<PostsDto> getPostById(@PathVariable("id") Long id){
        PostsDto postsDto = postsService.getPostsDtoById(id);
        return ResponseEntity.ok(postsDto);
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id){
        postsService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    //수정
    @PutMapping("/{id}")
    public ResponseEntity<PostsDto> updatePosts(@PathVariable("id") Long id, @RequestBody
    PostsDto updatePostsDto){
        PostsDto updatedPostDto = postsService.updatePosts(id,updatePostsDto);

        return ResponseEntity.ok(updatedPostDto);
    }


}
