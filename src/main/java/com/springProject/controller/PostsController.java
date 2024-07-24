package com.springProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/search")  // → RequestParam
	public ResponseEntity<List<PostsDto>> findPostsBySearchData(@ModelAttribute SearchData searchData) {
		log.info("category = %s, location = %s, hashtags = %s, startdate = %s, enddate = %s",
			searchData.getCategory(), searchData.getLocation(), searchData.getHashtag(), searchData.getStartDate(), searchData.getEndDate());

		List<PostsDto> posts = postsService.findPostsBySearchData(searchData);
		return ResponseEntity.ok(posts);
	}


	@GetMapping("/sort/{keyword}")  // → RequestParam
	public ResponseEntity<List<PostsDto>> sortPosts(
		@PathVariable("keyword") String keyword) {
		List<PostsDto> posts = postsService.sortPosts(keyword);
		return ResponseEntity.ok(posts);
	}

}
