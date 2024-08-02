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
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookMarksController {

    private final BookMarksService bookMarksService;

    @GetMapping("/get")
    public ResponseEntity<List<BookMarksDto>> getBookMarks(@RequestParam Long userId) {
        List<BookMarksDto> bookMarksDtoList = bookMarksService.findAllById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(bookMarksDtoList);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<BookMarksDto> saveBookMarks(@RequestParam Long postId, @AuthenticationPrincipal UserDetails user) {

        BookMarksDto bookMarksDto = bookMarksService.save(postId, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(bookMarksDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<String> deleteBookMarks(@RequestParam Long bookMarkId, @AuthenticationPrincipal UserDetails user) {
        bookMarksService.delete(bookMarkId, user.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }
}
