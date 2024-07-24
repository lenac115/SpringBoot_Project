package com.springProject.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;
import com.springProject.repository.PostsRepository;
import com.springProject.PostsSpecification;
import com.springProject.repository.PostsRepositoryImpl;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostsService {

	private final PostsRepository postsRepository;

	@Autowired
	public PostsService(PostsRepository postsRepository) {
		this.postsRepository = postsRepository;
	}


	// 검색 조건에 맞게 데이터 검색하는 메서드
	public List<PostsDto> getPostsBySearchDataAndSortBy(SearchData searchData, String sortBy, int nowPage) {
		log.info("category = {}, location = {}, star = {}, hashtags = {}, startdate = {}, enddate = {}, sortBy = {}, page = {}",
			searchData.getCategory(), searchData.getLocation(), searchData.getStar(), searchData.getHashtag(),
			searchData.getStartDate(), searchData.getEndDate(), sortBy, nowPage);

		Pageable pageable = PageRequest.of(nowPage-1, 12);

		Page<Posts> page = postsRepository.searchPosts(searchData, sortBy, pageable);
		page.hasPrevious();
		page.hasNext();

		page.forEach(p -> log.info("title = {}, star = {}", p.getTitle(), p.getStar()));
		return page.stream()
			.map(PostsDto::convertToDto)
			.collect(Collectors.toList());
	}


}
