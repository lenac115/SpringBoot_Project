package com.springProject.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.springProject.SearchData;

@SpringBootTest
class PostsServiceTest {

  @Autowired
  private PostsService postsService;

  @Test
  public void testGetPostsBySearchDataAndSortBy(SearchData searchData) {
    //SearchData searchData = new SearchData(null, null, 0, null, null, null);
    //postsService.getPostsBySearchDataAndSortBy(searchData,"newPost", 1);
  }
}
