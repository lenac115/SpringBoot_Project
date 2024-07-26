package com.springProject.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
	public String getPostsBySearchDataAndSortBy(
		@ModelAttribute SearchData searchData,
		@RequestParam(value = "sort", defaultValue = "newPost") String sortBy,
		@RequestParam(value="page") int nowPage,
		Model model) {
		log.info("keyword = {}, category = {}, location = {}, star = {}, "
				+ "hashtags = {}, startdate = {}, enddate = {}, sortBy = {}, page = {}",
			searchData.getKeyword(), searchData.getCategory(), searchData.getLocation(),
			searchData.getStar(), searchData.getHashtag(),
			searchData.getStartDate(), searchData.getEndDate(), sortBy, nowPage);

		List<PostsDto> notices = postsService.getFirst5ByIsNoticeTrueOrderByCreated_atDesc();
		List<PostsDto> posts = postsService.getPostsBySearchDataAndSortBy(searchData, sortBy, nowPage);

		model.addAttribute("notices", notices);
		model.addAttribute("posts", posts);

		return "post/search";
	}


}
