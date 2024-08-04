package com.springProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.service.PostsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostsController {

    private final PostsService postsService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    //생성
    @PostMapping
    public ResponseEntity<PostsDto> createPost(@RequestBody PostsDto postsDto,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        PostsDto createdPostDto = postsService.createPost(postsDto, userDetails.getUsername());
        return ResponseEntity.ok(createdPostDto);
    }

    //조회
    @GetMapping
    public ResponseEntity<List<PostsDto>> getAllPosts() {
        List<PostsDto> postsDto = postsService.getAllPosts();
        return ResponseEntity.ok(postsDto);
    }

    //상세조회
    @GetMapping("/{id}")
    public ResponseEntity<PostsDto> getPostById(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PostsDto postsDto = postsService.getPostsDtoById(id, userDetails);

        return ResponseEntity.ok(postsDto);
    }


    @GetMapping("/get")
    public ModelAndView getPostDetails(@RequestParam Long postId, Model model) {
        model.addAttribute("id", postId);
        return new ModelAndView("postsDetails/myPost");
    }

    @GetMapping("/updateForm") // 배포 후에 user 검증 넣을 예정
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ModelAndView getPostUpdateForm(@RequestParam Long postId) {
        return new ModelAndView("postsDetails/postUpdateForm");
    }

    //수정
    @PutMapping("/{id}")
    public ResponseEntity<PostsDto> updatePosts(@PathVariable("id") Long id, @RequestBody
    PostsDto updatePostsDto) {
        PostsDto updatedPostDto = postsService.updatePosts(id, updatePostsDto);

        return ResponseEntity.ok(updatedPostDto);
    }

    // ModelAttribute → 검색 조건을 받아옴 / RequestParam -> 정렬 조건을 받아옴
    @GetMapping("/search")
    public ResponseEntity<List<PostsDto>> getPostsBySearchDataAndSortBy(@ModelAttribute SearchData searchData,
                                                                        @RequestParam(value = "sort", defaultValue = "newPost") String sortBy,
                                                                        @RequestParam(value = "page") int nowPage) {
        log.info("category = {}, location = {}, star = {}, hashtags = {}, startdate = {}, enddate = {}, sortBy = {}, page = {}",
                searchData.getCategory(), searchData.getLocation(), searchData.getStar(), searchData.getHashtag(),
                searchData.getStartDate(), searchData.getEndDate(), sortBy, nowPage);

        List<PostsDto> posts = postsService.getPostsBySearchDataAndSortBy(searchData, sortBy, nowPage);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole('ROLE_user', 'ROLE_admin')")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails users) {

        postsService.deletePost(postId, users.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }

    @PostMapping("/notice/save")
    @PreAuthorize("hasAnyRole('ROLE_admin')")
    public ResponseEntity<PostsDto> createNotice(@RequestBody PostsDto postsDto, @AuthenticationPrincipal UserDetails users) {

        PostsDto savedPost = postsService.createNotice(postsDto, users.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(savedPost);
    }

    @PutMapping("/notice/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_admin')")
    public ResponseEntity<PostsDto> updateNotice(@RequestBody PostsDto postsDto,
                                                 @AuthenticationPrincipal UserDetails users, @PathVariable Long id) {

        PostsDto savedPost = postsService.updateNotice(postsDto, users.getUsername(), id);

        return ResponseEntity.status(HttpStatus.OK).body(savedPost);
    }

    @DeleteMapping("/notice/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_admin')")
    public ResponseEntity<String> deleteNotice(@AuthenticationPrincipal UserDetails users, @PathVariable Long id) {

        postsService.deleteNotice(users.getUsername(), id);

        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }
}
