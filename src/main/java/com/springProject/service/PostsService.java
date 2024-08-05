package com.springProject.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.springProject.dto.UsersDto;
import com.springProject.entity.BookMarks;
import com.springProject.entity.Prefers;
import com.springProject.entity.Users;
import com.springProject.repository.BannedUserRepository;
import com.springProject.repository.UsersRepository;
import com.springProject.utils.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;
import com.springProject.repository.PostsRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostsService {

    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;
    private final BannedUserRepository bannedUserRepository;

    public PostsDto createPost(PostsDto postsDto, String username) {
        Posts post = ConvertUtils.convertDtoToPosts(postsDto);
        Users findUsers = usersRepository.findByLoginId(username);

        post.setCreated_at(new Timestamp(System.currentTimeMillis()));
        post.setUsers(findUsers);
        findUsers.getPosts().add(post);
        postsRepository.save(post);
        return ConvertUtils.convertPostsToDto(post);
    }

    public List<PostsDto> getAllPosts() {
        return postsRepository.findAll()
                .stream()
                .map(ConvertUtils::convertPostsToDto)
                .collect(Collectors.toList());
    }

    public PostsDto getPostsDtoById(Long id, UserDetails users) {
        Posts findPost = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        PostsDto postsDto = ConvertUtils.convertPostsToWith(findPost);
        if(users != null) {
            Users findUser = usersRepository.findByLoginId(users.getUsername());
            postsDto.setPresentId(users.getUsername());

            List<BookMarks> bookMarksList = findPost.getBookMarks();
            List<Prefers> prefersList = findPost.getPrefers();
            for(BookMarks bookMarks : bookMarksList) {
                if(bookMarks.getUsers().getId().equals(findUser.getId()))
                    postsDto.setBookmark(true);
            }
            for(Prefers prefers : prefersList) {
                if(prefers.getUsers().getId().equals(findUser.getId()))
                    postsDto.setPrefers(true);
            }
        }

        return postsDto;
    }

    // delete
    public void deletePost(Long postId, String username) {

        isBanned();

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
    }


    public PostsDto updatePosts(Long id, PostsDto updatePostsDto) {
        Posts post = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        post.setTitle(updatePostsDto.getTitle());
        post.setBody(updatePostsDto.getBody());
        post.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        post.setLocation(updatePostsDto.getLocation());
        post.setCategory(updatePostsDto.getCategory());
        post.setStar(updatePostsDto.getStar());
        post.setHashtags(updatePostsDto.getHashtags());
        return ConvertUtils.convertPostsToDto(post);
    }

    // 검색 조건에 맞게 데이터 검색하는 메서드
    @Transactional(readOnly = true)
    public Page<PostsDto> getPostsBySearchDataAndSortBy(SearchData searchData, String sortBy, int nowPage) {

		isBanned();

        // 페이징을 위한 기본 설정 -> (보여줄 페이지, 한 페이지에 보여줄 데이터 수)
        Pageable pageable = PageRequest.of(nowPage - 1, 12);

		// 검색 및 정렬 기능 수행 후 설정된 pageable에 맞게 페이지 반환
		Page<PostsDto> page = postsRepository.searchPosts(searchData, sortBy, pageable);
        log.info("getSize={}, getTotalPages={}, total ={}", page.getSize(), page.getTotalPages(), page.getTotalElements());

        return page;
    }


    // 공지사항 따로 추출
    @Transactional(readOnly = true)
    public List<PostsDto> getNoticeFive() {
        return postsRepository.getNoticeFive()
            .stream()
            .map(ConvertUtils::convertPostsToDto)
            .collect(Collectors.toList());
    }

    // 공지사항 포스팅
    public PostsDto createNotice(PostsDto postsDto, String username) {

		isBanned();

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

		isBanned();

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

		isBanned();

        Posts findPosts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Users findUsers = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if(findPosts.isNotice()) {
            throw new IllegalArgumentException("수정 가능한 게시물이 아닙니다.");
        }
        if(!findUsers.getAuth().equals(Users.UserAuth.admin)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        postsRepository.delete(findPosts);
    }

    public List<PostsDto> getNotice() {
        isBanned();
        return Optional.ofNullable(postsRepository.findAllByNotice())
                .orElseGet(Collections::emptyList).stream().map(ConvertUtils::convertPostsToDto).toList();
    }

    private void isBanned() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        Users findUser = usersRepository.findOptionalByLoginId(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if (findUser.getBannedUser() == null)
            return;
        if (LocalDateTime.now().isAfter(findUser.getBannedUser().getBannedDate())) {
            findUser.setIsActivated(true);
            bannedUserRepository.deleteByUsersId(findUser.getId());
            return;
        }

        throw new AccessDeniedException("정지된 사용자입니다.");
    }

    public Boolean isEqual(UsersDto usersDto, String username) {
        return usersDto.getLoginId().equals(username);
    }

    public List<PostsDto> getAllNotice() {
        List<Posts> posts = postsRepository.getAllNotice();
        return posts.stream()
                .map(ConvertUtils::convertPostsToDto)
                .collect(Collectors.toList());
    }

    public List<PostsDto> getPostsList() {
        List<Posts> posts = postsRepository.getAllPosts();
        return posts.stream()
                .map(ConvertUtils::convertPostsToWith)
                .collect(Collectors.toList());
    }

    public List<PostsDto> getSearchPosts(String title) {
        List<Posts> posts = postsRepository.searchByTitleLike(title);
        return posts.stream()
                .map(ConvertUtils::convertPostsToWith)
                .collect(Collectors.toList());
    }
}
