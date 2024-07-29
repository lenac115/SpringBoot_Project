package com.springProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.service.PostsService;

import lombok.extern.slf4j.Slf4j;

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
    public ResponseEntity<PostsDto> createPost(@RequestBody PostsDto postsDto) {
        PostsDto createdPostDto = postsService.createPost(postsDto);
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
    public ResponseEntity<PostsDto> getPostById(@PathVariable("id") Long id){
        PostsDto postsDto = postsService.getPostsDtoById(id);
        return ResponseEntity.ok(postsDto);
    }

    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id){
        postsService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    //수정
    @PutMapping("/{id}")
    public ResponseEntity<PostsDto> updatePosts(@PathVariable("id") Long id, @RequestBody
    PostsDto updatePostsDto){
        PostsDto updatedPostDto = postsService.updatePosts(id,updatePostsDto);

        return ResponseEntity.ok(updatedPostDto);
    }

	// 검색 결과 추출 컨트롤러
	// ModelAttribute → 검색 조건을 받아옴 / RequestParam -> 정렬 조건을 받아옴
	@GetMapping("/search")
	public ResponseEntity<List<PostsDto>> getPostsBySearchDataAndSortBy(@ModelAttribute SearchData searchData,
		@RequestParam(value = "sort", defaultValue = "newPost") String sortBy,
		@RequestParam(value="page") int nowPage) {
		log.info("category = {}, location = {}, star = {}, hashtags = {}, startdate = {}, enddate = {}, sortBy = {}, page = {}",
			searchData.getCategory(), searchData.getLocation(), searchData.getStar(), searchData.getHashtag(),
			searchData.getStartDate(), searchData.getEndDate(), sortBy, nowPage);

		List<PostsDto> posts = postsService.getPostsBySearchDataAndSortBy(searchData, sortBy, nowPage);
		return ResponseEntity.ok(posts);
	}

	// 공지사항 추출 컨트롤러
	@GetMapping("/search")
	public ResponseEntity<List<PostsDto>> getNoticeFive() {
		List<PostsDto> notices = postsService.getNoticeFive();
		return ResponseEntity.ok(notices);
	}

	@DeleteMapping("/{postId}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<String> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails users) {

		postsService.deletePost(postId, users.getUsername());
		return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
	}

	@PostMapping("/notice/save")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<PostsDto> createNotice(@RequestBody PostsDto postsDto, @AuthenticationPrincipal UserDetails users) {

		PostsDto savedPost = postsService.createNotice(postsDto, users.getUsername());

		return ResponseEntity.status(HttpStatus.OK).body(savedPost);
	}

	@PutMapping("/notice/update/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<PostsDto> updateNotice(@RequestBody PostsDto postsDto,
												 @AuthenticationPrincipal UserDetails users, @PathVariable Long id) {

		PostsDto savedPost = postsService.updateNotice(postsDto, users.getUsername(), id);

		return ResponseEntity.status(HttpStatus.OK).body(savedPost);
	}

	@DeleteMapping("/notice/delete/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<String> deleteNotice(@AuthenticationPrincipal UserDetails users, @PathVariable Long id) {

		postsService.deleteNotice(users.getUsername(), id);

		return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
	}

}
