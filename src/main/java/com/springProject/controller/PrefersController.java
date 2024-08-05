package com.springProject.controller;

import com.springProject.dto.BookMarksDto;
import com.springProject.dto.PrefersDto;
import com.springProject.service.PrefersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prefers")
public class PrefersController {

    private final PrefersService prefersService;

    @GetMapping("/counts")
    public ResponseEntity<String> countPrefers(@RequestParam Long postId) {
        String countNum = prefersService.countById(postId).toString();

        return ResponseEntity.status(HttpStatus.OK).body(countNum);
    }

    @PostMapping("/get")
    public ResponseEntity<List<PrefersDto>> getAllPrefers(@RequestParam Long userId) {
        List<PrefersDto> savedPrefers = prefersService.getAllPrefers(userId);

        return ResponseEntity.status(HttpStatus.OK).body(savedPrefers);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<PrefersDto> saveBookMarks(@RequestParam Long postId, @AuthenticationPrincipal UserDetails user) {

        PrefersDto prefersDto = prefersService.save(postId, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(prefersDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_user')")
    public ResponseEntity<String> deleteBookMarks(@RequestParam Long postId, @AuthenticationPrincipal UserDetails user) {
        prefersService.delete(postId, user.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }
}
