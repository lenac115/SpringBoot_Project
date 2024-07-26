package com.springProject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;
import com.springProject.repository.PostsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostsService {

	private final PostsRepository postsRepository;

	@Autowired
	public PostsService(PostsRepository postsRepository) {
		this.postsRepository = postsRepository;
	}


	// 공지사항 출력 용 메서드
	public List<PostsDto> getFirst5ByIsNoticeTrueOrderByCreated_atDesc() {
		return postsRepository.getFirst5ByIsNoticeTrueOrderByCreated_atDesc()
			.stream().map(PostsDto::convertToDto)
			.collect(Collectors.toList());
	}

	// 검색 조건에 맞게 데이터 검색하는 메서드
	public List<PostsDto> getPostsBySearchDataAndSortBy(SearchData searchData, String sortBy, int nowPage) {

		// 페이징을 위한 기본 설정 -> (보여줄 페이지, 한 페이지에 보여줄 데이터 수)
		Pageable pageable = PageRequest.of(nowPage-1, 12);

		// 검색 및 정렬 기능 수행 후 설정된 pageable에 맞게 페이지 반환
		Page<Posts> page = postsRepository.searchPosts(searchData, sortBy, pageable);
		page.isEmpty(); // 페이지가 비어있는지 확인
		page.getTotalPages(); // 전체 페이지 개수 확인
		page.hasPrevious(); // 이전 블록 존재 여부 확인
		page.hasNext(); // 다음 블록 존재 여부 확인

		page.forEach(p -> log.info("title = {}, star = {}", p.getTitle(), p.getStar()));
		return page.stream()
			.map(PostsDto::convertToDto)
			.collect(Collectors.toList());
	}


}
