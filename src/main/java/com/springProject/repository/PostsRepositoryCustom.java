package com.springProject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springProject.SearchData;
import com.springProject.entity.Posts;

public interface PostsRepositoryCustom {
	Page<Posts> searchPosts(SearchData searchData, String sortBy, Pageable pageable);
}
