package com.springProject.service;

import com.springProject.dto.CommentWithParent;
import com.springProject.dto.CommentsDto;
import com.springProject.entity.Comments;
import com.springProject.entity.Posts;
import com.springProject.entity.Users;
import com.springProject.repository.BannedUserRepository;
import com.springProject.repository.CommentsRepository;
import com.springProject.repository.PostsRepository;
import com.springProject.repository.UsersRepository;
import com.springProject.utils.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final BannedUserRepository bannedUserRepository;


    // 계층형 댓글 출력
    @Transactional(readOnly = true)
    public List<CommentWithParent> findCommentsByPostId(Long id, UserDetails userDetails) {
        isBanned();
        // 쿼리 결과값
        List<CommentWithParent> result = new ArrayList<>();
        // 정렬을 위한 Map
        Map<Long, CommentWithParent> map = new HashMap<>();
        // post의 id를 기준으로 comment list를 가져옴
        List<Comments> comments = commentsRepository.findByPostId(id);

        comments.forEach(c -> {
                    CommentWithParent commentWithParent;

                    // 삭제된 코멘트를 판별하여 해당 comments list의 원소의 데이터를 CommentWithParent dto로 변환하여 생성
                    if(c.isActivated()) {
                        commentWithParent = CommentWithParent.builder()
                                .body(c.getBody())
                                .id(c.getId())
                                .author(c.getUsers().getNickname())
                                .loginId(c.getUsers().getLoginId())
                                .updatedAt(c.getUpdatedAt())
                                .depth(c.getDepth())
                                .children(new ArrayList<>())
                                .isActivated(c.isActivated())
                                .build();
                    } else {
                        commentWithParent = CommentWithParent.builder()
                                .body("삭제된 댓글입니다.")
                                .id(c.getId())
                                .author(c.getUsers().getNickname())
                                .loginId(c.getUsers().getLoginId())
                                .updatedAt(c.getUpdatedAt())
                                .depth(c.getDepth())
                                .children(new ArrayList<>())
                                .isActivated(c.isActivated())
                                .build();
                    }
                    if(userDetails != null) {
                        commentWithParent.setPresentId(userDetails.getUsername());
                    }

                    // 부모가 존재하는 경우 부모의 id값 저장
                    if(c.getParent() != null){
                        commentWithParent.setParent(c.getParent().getId());
                    }

                    // Map에 부모의 id와 dto 저장
                    map.put(commentWithParent.getId(), commentWithParent);

                    // 부모가 존재하는 경우, 해당 부모의 id값에 해당하는 원소에 자식 리스트에 dto 추가, 아니라면 result에 추가
                    if (c.getParent() != null)
                        map.get(c.getParent().getId()).getChildren().add(commentWithParent);
                    else result.add(commentWithParent);
                }
        );

        // 정렬된 comments list 반환
        return result;
    }


    // 업데이트, 내용을 업데이트하고 updateAt 갱신
    public CommentsDto update(Long id, CommentsDto commentsDto, String username) {
        isBanned();
        Comments updatedComments = commentsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Users findUsers = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if(!findUsers.getIsActivated())
            throw new AccessDeniedException("정지된 사용자입니다.");

        // 코멘트의 user와 수정을 시도하는 user가 동일한지 검사
        if(findUsers.getId().equals(updatedComments.getUsers().getId())){
            updatedComments.setBody(commentsDto.getBody());
            updatedComments.setUpdatedAt(LocalDateTime.now());
        } else {
            throw new AccessDeniedException("권한이 없습니다");
        }
        return ConvertUtils.convertCommentsToDto(updatedComments);
    }

    // 댓글 삭제, 부모 댓글이 삭제되면 계층형 댓글 구조에 문제가 생겨서 Activated를 false 처리
    public void delete(Long id, String username) {
        isBanned();
        Comments findComments = commentsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Users findUsers = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if(!findUsers.getIsActivated())
            throw new AccessDeniedException("정지된 사용자입니다.");

        // admin 권한이거나 코멘트 작성 user와 동일한 user이면 비활성화
        if(findUsers.getAuth() == Users.UserAuth.admin) {
            findComments.setActivated(false);
        } else if(findUsers.getId().equals(findComments.getUsers().getId())) {
            findComments.setActivated(false);
        } else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }

    // 부모가 없는 댓글 생성
    public CommentsDto nonReply(String username, CommentsDto commentsDto, Long postId) {
        isBanned();
        commentsDto.setCreatedAt(LocalDateTime.now());
        commentsDto.setUpdatedAt(LocalDateTime.now());

        Users findUser = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Posts findPost = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        // 만들 댓글의 정보를 commments로 convert
        Comments createdComments = ConvertUtils.convertDtoToComments(commentsDto);
        createdComments.setActivated(true);

        // 만든 댓글의 연관관계를 설정
        createdComments.setUser(findUser);
        createdComments.setPost(findPost);

        System.out.println(createdComments.getBody());


        // 영속화
        commentsRepository.save(createdComments);


        return ConvertUtils.convertCommentsToDto(commentsRepository.save(createdComments));
    }

    // 부모가 존재하는 댓글 설정
    public CommentsDto reply(Long postId, Long commentId, CommentsDto commentsDto, String username) {
        isBanned();
        commentsDto.setCreatedAt(LocalDateTime.now());
        commentsDto.setUpdatedAt(LocalDateTime.now());

        Users findUser = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Posts findPost = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        // 만들 댓글의 정보를 comments로 convert
        Comments createdComments = ConvertUtils.convertDtoToComments(commentsDto);
        createdComments.setActivated(true);

        // 연관관계 설정, 부모가 존재하는 댓글이기 때문에 comments 도메인 내부의 연관관계도 설정
        createdComments.setUser(usersRepository.findByLoginId(username)); // 임시 메소드
        createdComments.setPost(postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
        createdComments.addReplyComment(commentsRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
        // 영속화
        commentsRepository.save(createdComments);

        return ConvertUtils.convertCommentsToDto(createdComments);
    }

    private void isBanned() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        System.out.println(authentication.getName());
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
}
