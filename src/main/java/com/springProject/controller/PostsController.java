package com.springProject.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.service.PostsService;

import lombok.extern.slf4j.Slf4j;


import org.springframework.web.bind.annotation.*;

@Slf4j
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

	// ModelAttribute → 검색 조건을 받아옴 / RequestParam -> 정렬 조건을 받아옴
	@GetMapping("/search")
	public ResponseEntity<List<PostsDto>> getPostsBySearchDataAndSortBy(@ModelAttribute SearchData searchData,
		@RequestParam(value = "sort", defaultValue = "newPost") String sortBy,
		@RequestParam(value="page") int nowPage) {
		log.info("category = {}, location = {}, star = {}, hashtags = {}, startdate = {}, enddate = {}, sortBy = {}, page = {}",
			searchData.getCategory(), searchData.getLocation(), searchData.getStar(), searchData.getHashtag(),
			searchData.getStartDate(), searchData.getEndDate(), sortBy, nowPage);

		List<PostsDto> posts = postsService.getPostsBySearchDataAndSortBy(searchData, sortBy, nowPage);
		return ResponseEntity.ok(posts);
	}


}
