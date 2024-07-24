package com.springProject.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PostsControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void getPostsBySearchDataAndSortBy() throws Exception {
    mockMvc.perform(get(
                "/api/posts/search?category=null&location=null&star=0&hashtag=null&startDate=null&endDate=null&sort=&page=1"))
        .andExpect(status().isOk());
        //.andDo(result -> System.out.println(result.getResponse().getContentAsString()));
  }
}
