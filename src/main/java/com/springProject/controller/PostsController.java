package com.springProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.springProject.SearchData;
import com.springProject.dto.PostsDto;
import com.springProject.entity.Posts;
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
    public ResponseEntity<PostsDto> createPost(@RequestBody PostsDto postsDto, @AuthenticationPrincipal UserDetails userDetails) {
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
        PostsDto postsDto = postsService.getPostsDtoById(id);
		if (userDetails != null) {
			postsDto.setEqual(postsService.isEqual(postsDto.getUsersDto(), "1234@naver.com"));
		}
		return ResponseEntity.ok(postsDto);
    }

    //삭제
	@DeleteMapping("/{postId}")
	@PreAuthorize("hasAnyRole('ROLE_user', 'ROLE_admin')")
	public ResponseEntity<String> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails users) {
		postsService.deletePost(postId, users.getUsername());
		return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }

    //수정
    @PutMapping("/{id}")
    public ResponseEntity<PostsDto> updatePosts(@PathVariable("id") Long id, @RequestBody
    PostsDto updatePostsDto) {
        PostsDto updatedPostDto = postsService.updatePosts(id, updatePostsDto);

        return ResponseEntity.ok(updatedPostDto);
    }

	@GetMapping("/get")
	public ModelAndView getPostDetails(@RequestParam Long postId, Model model) {
		model.addAttribute("id", postId);
		return new ModelAndView("postsDetails/myPost");
	}

	@GetMapping("/updateForm/*") // 배포 후에 user 검증 넣을 예정
	public ModelAndView getPostUpdateForm() {
		return new ModelAndView("postsDetails/postUpdateForm");
	}

    // ModelAttribute → 검색 조건을 받아옴 / RequestParam -> 정렬 조건을 받아옴
    @GetMapping("/search")
    public ModelAndView getPostsBySearchDataAndSortBy(@ModelAttribute SearchData searchData,
         @RequestParam(value = "sort", defaultValue = "newPost", required = false) String sortBy,
         @RequestParam(value = "page", defaultValue = "1", required = false) int nowPage,
		 Model model) {
        log.info("category = {}, location = {}, star = {}, hashtags = {}, startdate = {}, enddate = {}, sortBy = {}, page = {}",
                searchData.getCategory(), searchData.getLocation(), searchData.getStar(), searchData.getHashtag(),
                searchData.getStartDate(), searchData.getEndDate(), sortBy, nowPage);

		Page<PostsDto> posts = postsService.getPostsBySearchDataAndSortBy(searchData, sortBy, nowPage);
		model.addAttribute("page", posts);

		List<PostsDto> notices = postsService.getNoticeFive();
		model.addAttribute("notices", notices);

		getPostsBySearchDataAndSortBy(posts);
		getNoticeFive(notices);

		return new ModelAndView("post/search");
	}

	// HTTP 전송 용 코드
	@ResponseBody
	public ResponseEntity<Page<PostsDto>> getPostsBySearchDataAndSortBy(Page<PostsDto> posts)
	{
		return ResponseEntity.ok(posts);
	}

	@ResponseBody
	public ResponseEntity<List<PostsDto>> getNoticeFive(List<PostsDto> notices)
	{
		return ResponseEntity.ok(notices);
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
