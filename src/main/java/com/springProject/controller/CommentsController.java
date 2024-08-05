package com.springProject.controller;

import com.springProject.dto.CommentWithParent;
import com.springProject.dto.CommentsDto;
import com.springProject.service.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CommentsController {

    private final CommentsService commentsService;

    // post에 귀속된 comment 출력
    @GetMapping("/get/{postId}")
    public ResponseEntity<List<CommentWithParent>> getComments(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        List<CommentWithParent> commentsDtoList = commentsService.findCommentsByPostId(postId, userDetails);
       /* if(userDetails == null){
            commentsDtoList = commentsService.findCommentsByPostId(postId);
        } else {
           commentsDtoList = commentsService.findCommentsByPostId(postId, userDetails.getUsername());
        }*/

        return ResponseEntity.status(HttpStatus.OK).body(commentsDtoList);
    }

    // comment 수정
    @PutMapping("/update/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_user', 'ROLE_admin')")
    public ResponseEntity<CommentsDto> updateComment(@PathVariable Long commentId,
                                                     @RequestBody CommentsDto commentsDto,
                                                     @AuthenticationPrincipal UserDetails users) {

        CommentsDto updatedDto = commentsService.update(commentId, commentsDto, users.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // comment 삭제
    @DeleteMapping("/delete/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_user', 'ROLE_admin')")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId, @AuthenticationPrincipal UserDetails users) {

        commentsService.delete(commentId, users.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
    }


    @PostMapping("/nonreply")
    @PreAuthorize("hasAnyRole('ROLE_user', 'ROLE_admin')")
    public ResponseEntity<CommentsDto> createNonReplyComment(@RequestParam Long postId,
                                                             @RequestBody CommentsDto commentsDto, @AuthenticationPrincipal UserDetails user) {

        CommentsDto createdComments = commentsService.nonReply(user.getUsername(), commentsDto, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdComments);
    }

    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('ROLE_user', 'ROLE_admin')")
    public ResponseEntity<CommentsDto> createReplyComment(@RequestParam Long postId, @RequestParam Long commentId,
                                                          @RequestBody CommentsDto commentsDto, @AuthenticationPrincipal UserDetails user) {
        CommentsDto createdComments = commentsService.reply(postId, commentId, commentsDto, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdComments);
    }
}
