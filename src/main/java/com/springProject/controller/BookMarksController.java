package com.springProject.controller;

import com.springProject.dto.BookMarksDto;
import com.springProject.service.BookMarksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookMarksController {

    private final BookMarksService bookMarksService;

    @GetMapping
    public ResponseEntity<List<BookMarksDto>> getBookMarks(@RequestParam(value="userId") Long userId) {
        List<BookMarksDto> bookMarksDtoList = bookMarksService.findAllById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(bookMarksDtoList);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getBookMarksById(@RequestParam(value="postId") Long postId, @AuthenticationPrincipal UserDetails user) {

        Boolean isBookMark = bookMarksService.isBookMark(postId, user.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body("bookmark 찾았어요!");
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<BookMarksDto> saveBookMarks(@RequestParam(value="postId") Long postId, @AuthenticationPrincipal UserDetails user) {

        BookMarksDto bookMarksDto = bookMarksService.save(postId, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(bookMarksDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<String> deleteBookMarks(@RequestParam(value="postId") Long postId, @AuthenticationPrincipal UserDetails user) {
        bookMarksService.delete(postId, user.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }
}
