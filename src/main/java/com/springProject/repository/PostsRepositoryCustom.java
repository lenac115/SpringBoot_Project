package com.springProject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;

public interface PostsRepositoryCustom {
	Page<PostsDto> searchPosts(SearchData searchData, String sortBy, Pageable pageable);

}
