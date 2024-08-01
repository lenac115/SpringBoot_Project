package com.springProject.service;


import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;
import com.springProject.repository.PostsRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.springProject.entity.Users;
import com.springProject.repository.UsersRepository;
import com.springProject.utils.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.springProject.SearchData;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostsService {

    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;


    public PostsDto createPost(PostsDto postsDto) {
        Posts post = ConvertUtils.convertDtoToPosts(postsDto);
        post.setCreated_at(new Timestamp(System.currentTimeMillis()));
        postsRepository.save(post);
        return ConvertUtils.convertPostsToDto(post);
    }

    public List<PostsDto> getAllPosts() {
        return postsRepository.findAll()
                .stream()
                .map(ConvertUtils::convertPostsToDto)
                .collect(Collectors.toList());
    }

    public PostsDto getPostsDtoById(Long id) {
        return postsRepository.findById(id)
                .stream()
                .filter(post -> post.getUser_id().equals(id))
                .findFirst()
                .map(ConvertUtils::convertPostsToDto)
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 글을 찾을 수 없습니다."));
    }

    public void deletePost(Long id) {
        postsRepository.findById(id).map(post -> {
                    postsRepository.delete(post);
                    return true;
                })
                .orElse(false);
    }

    private Posts findPostById(Long id) {
        return postsRepository.findById(id)
                .stream()
                .filter(post -> post.getPost_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 글을 찾을 수 없습니다."));
    }

    public PostsDto updatePosts(Long id, PostsDto updatePostsDto) {
        return postsRepository.findById(id)
                .map(existingposts -> {
                    existingposts.setTitle(updatePostsDto.getTitle());
                    existingposts.setBody(updatePostsDto.getBody());
                    existingposts.setUpdated_at(updatePostsDto.getUpdatedAt());
                    return PostsDto(postsRepository.save(existingposts));
                });
    }


    // 검색 조건에 맞게 데이터 검색하는 메서드
    @Transactional(readOnly = true)
    public List<PostsDto> getPostsBySearchDataAndSortBy(SearchData searchData, String sortBy, int nowPage) {

        // 페이징을 위한 기본 설정 -> (보여줄 페이지, 한 페이지에 보여줄 데이터 수)
        Pageable pageable = PageRequest.of(nowPage - 1, 12);

		// 검색 및 정렬 기능 수행 후 설정된 pageable에 맞게 페이지 반환
		Page<Posts> page = postsRepository.searchPosts(searchData, sortBy, pageable);
		page.isEmpty(); // 페이지가 비어있는지 확인
		page.getTotalPages(); // 전체 페이지 개수 확인
		page.hasPrevious(); // 이전 블록 존재 여부 확인
		page.hasNext(); // 다음 블록 존재 여부 확인

        page.forEach(p -> log.info("title = {}, star = {}", p.getTitle(), p.getStar()));
        return page.stream()
                .map(ConvertUtils::convertPostsToDto)
                .collect(Collectors.toList());
    }

    // 공지사항 따로 추출
    public List<PostsDto> getNoticeFive() {
        return postsRepository.getNoticeFive()
            .stream()
            .map(ConvertUtils::convertPostsToDto)
            .collect(Collectors.toList());
    }

    // delete
    public void deletePost(Long postId, String username) {

        // 도메인 불러오기
        Users findUser = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Posts post = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if(findUser.getAuth() == Users.UserAuth.admin) {
            postsRepository.delete(post);
        } else if (findUser.getId().equals(post.getUsers().getId())) {
            postsRepository.delete(post);
        } else{
            throw new AccessDeniedException("권한이 없습니다.");
        }

        postsRepository.delete(postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
    }

    // 공지사항 포스팅
    public PostsDto createNotice(PostsDto postsDto, String username) {

        Users findUser = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Posts savePost = postsRepository.save(ConvertUtils.convertDtoToPosts(postsDto));

        // 연관관계 설정
        findUser.getPosts().add(savePost);
        savePost.setUsers(findUser);

        // posts 기본값 설정
        savePost.setCreated_at(new Timestamp(System.currentTimeMillis()));
        savePost.setNotice(true);

        return ConvertUtils.convertPostsToDto(savePost);
    }

    public PostsDto updateNotice(PostsDto postsDto, String username, Long id) {

        Posts findPosts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Users findUsers = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if(!findPosts.isNotice()) {
            throw new IllegalArgumentException("수정 가능한 게시물이 아닙니다.");
        }
        if(!findUsers.getAuth().equals(Users.UserAuth.admin)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        findPosts.setTitle(postsDto.getTitle());
        findPosts.setStar(postsDto.getStar());
        findPosts.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        findPosts.setBody(postsDto.getBody());
        findPosts.setCategory(postsDto.getCategory());
        findPosts.setHashtags(postsDto.getHashtags());
        findPosts.setLocation(postsDto.getLocation());

        return ConvertUtils.convertPostsToDto(findPosts);
    }

    public void deleteNotice(String username, Long id) {

        Posts findPosts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Users findUsers = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if(!findPosts.isNotice()) {
            throw new IllegalArgumentException("수정 가능한 게시물이 아닙니다.");
        }
        if(!findUsers.getAuth().equals(Users.UserAuth.admin)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        postsRepository.delete(findPosts);
    }
}
