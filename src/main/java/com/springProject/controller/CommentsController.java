package com.springProject.controller;

import com.springProject.dto.CommentWithParent;
import com.springProject.dto.CommentsDto;
import com.springProject.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentsController {

    private final CommentsService commentsService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentWithParent>> getComments(@PathVariable Long postId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<CommentWithParent> commentsDtoList = commentsService.findCommentsByPostId(postId);

        return ResponseEntity.status(HttpStatus.OK).body(commentsDtoList);
    }

    @PutMapping("/{commentId}")
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentsDto> updateComment(@PathVariable Long commentId, @RequestBody CommentsDto commentsDto) {

        CommentsDto updatedDto = commentsService.update(commentId, commentsDto);

        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    @DeleteMapping("/{commentId}")
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {

        commentsService.delete(commentId);

        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }

    @PostMapping("/{postId}/nonReply")
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentsDto> createNonReplyComment(@PathVariable Long postId,
                                                             @RequestBody CommentsDto commentsDto, @AuthenticationPrincipal UserDetails user) {

        CommentsDto createdComments = commentsService.nonReply(user.getUsername(), commentsDto, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdComments);
    }

    @PostMapping("/{postId}/reply")
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentsDto> createReplyComment(@PathVariable Long postId, @RequestParam Long commentId,
                                                          @RequestBody CommentsDto commentsDto, @AuthenticationPrincipal UserDetails user) {
        CommentsDto createdComments = commentsService.reply(postId, commentId, commentsDto, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdComments);
    }
}
