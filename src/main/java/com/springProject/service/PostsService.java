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

@Service
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

}
