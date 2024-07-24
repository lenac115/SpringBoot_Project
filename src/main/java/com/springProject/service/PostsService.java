package com.springProject.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;
import com.springProject.repository.PostsRepository;
import com.springProject.repository.PostsSpecification;

@Service
public class PostsService {

	private final PostsRepository postsRepository;

	@Autowired
	public PostsService(PostsRepository postsRepository) {
		this.postsRepository = postsRepository;
	}

	// 검색 조건에 맞게 데이터 검색하는 메서드
	public List<PostsDto> findPostsBySearchData(SearchData searchData) {
		String category = searchData.getCategory();
		String location = searchData.getLocation();
		int star = searchData.getStar();
		String hashtag = searchData.getHashtag();
		Timestamp startDate = searchData.getStartDate();
		Timestamp endDate = searchData.getEndDate();

		// 동적으로 쿼리 생성 -> 데이터가 안들어오면 해당 파트는 검색 패스
		Specification<Posts> spec = (root, query, cb) -> null;
		spec = spec.and(PostsSpecification.greaterThanPostsStar(star));
		if (category != null) {
			spec = spec.or(PostsSpecification.equalPostsCategory(category));
		}
		if (location != null) {
			spec = spec.or(PostsSpecification.equalPostsLocation(location));
		}
		if (hashtag != null) {
			spec = spec.or(PostsSpecification.likePostsHashtag(hashtag));
		}
		// 시작점이 지정 안되었으면 첫번째 게시물부터 출력해야함
    	if (startDate == null)
			startDate = new Timestamp
				(1900,01,01,00,00,00,000);
		// 끝 점이 지정 안되었으면 지금까지 게시된 게시물까지 출력해야함
		if(endDate == null)
			endDate = new Timestamp(System.currentTimeMillis());
		spec = spec.and(PostsSpecification.betweenPostsDate(startDate, endDate));

		return postsRepository.findAll(spec).stream()
		  .map(PostsDto::convertToDto)
		  .collect(Collectors.toList());
	}

	public List<PostsDto> sortPosts(String keyword) {
		List<PostsDto> postsDtos = new ArrayList<>();
		return postsDtos;
	}
}
