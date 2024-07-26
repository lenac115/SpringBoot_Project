package com.springProject.service;


import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;
import com.springProject.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.springProject.SearchData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostsService {

    List<Posts> posts = new ArrayList<>();
    private Long nextPostId = 1L;

    public PostsDto createPost(PostsDto postsDto) {
        Posts post = convertToPostEntity(postsDto);
        post.setPost_id(nextPostId++);
        post.setCreated_at(new Timestamp(System.currentTimeMillis()));
        posts.add(post);
        return convertToPostsDto(post);
    }

    private static Posts convertToPostEntity(PostsDto postsDto) {
        Posts post = new Posts();
        post.setTitle(postsDto.getTitle());
        post.setBody(postsDto.getBody());
        return post;
    }

    public List<PostsDto> getAllPosts() {
        return posts.stream()
                .map(this::convertToPostsDto)
                .collect(Collectors.toList());
    }

    public PostsDto getPostsDtoById(Long id) {
        return posts.stream()
                .map(this::convertToPostsDto)
                .filter(post -> post.getUser_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 글을 찾을 수 없습니다."));
    }

    public void deletePost(Long id) {
        Posts post = findPostById(id);
        posts.remove(post);
    }

    private Posts findPostById(Long id) {
        return posts.stream()
                .filter(post -> post.getPost_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 글을 찾을 수 없습니다."));
    }

    public PostsDto updatePosts(Long id, PostsDto updatePostsDto) {
        Posts post = findPostById(id);
        post.setTitle(updatePostsDto.getTitle());
        post.setBody(updatePostsDto.getBody());
        post.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        return convertToPostsDto(post);
    }

    private PostsDto convertToPostsDto(Posts posts) {
        PostsDto postsDto = new PostsDto();
        return postsDto.builder()
                .user_id(posts.getUser_id())
                .title(posts.getTitle())
                .body(posts.getBody())
                .created_at(posts.getCreated_at())
                .updated_at(posts.getUpdated_at()).build();
    }


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
