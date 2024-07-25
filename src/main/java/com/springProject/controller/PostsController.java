package com.springProject.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.service.PostsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostsController {

	private final PostsService postsService;

	@Autowired
	public PostsController(PostsService postsService) {
		this.postsService = postsService;
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

	@DeleteMapping("/admin/{postId}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<String> deletePostByAdmin(@PathVariable Long postId) {
		postsService.deletePostByAdmin(postId);
		return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
	}

	@PostMapping("/admin/notice")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<PostsDto> createNotice(@RequestBody PostsDto postsDto) {
		postsService.createNotice(postsDto);
		return ResponseEntity.status(HttpStatus.OK).body(postsDto);
	}

}
