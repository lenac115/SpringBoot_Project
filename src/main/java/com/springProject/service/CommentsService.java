package com.springProject.service;

import com.springProject.dto.CommentWithParent;
import com.springProject.dto.CommentsDto;
import com.springProject.entity.Comments;
import com.springProject.repository.CommentsRepository;
import com.springProject.repository.PostsRepository;
import com.springProject.repository.UsersRepository;
import com.springProject.utils.ConvertUtils;
import lombok.RequiredArgsConstructor;
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


    // 계층형 댓글 출력
    @Transactional(readOnly = true)
    public List<CommentWithParent> findCommentsByPostId(Long id) {

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
                                .author(c.getUsers().getName())
                                .children(new ArrayList<>())
                                .build();
                    } else {
                        commentWithParent = CommentWithParent.builder()
                                .body("삭제된 댓글입니다.")
                                .id(c.getId())
                                .author(c.getUsers().getName())
                                .children(new ArrayList<>())
                                .build();
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
    public CommentsDto update(Long id, CommentsDto commentsDto) {
        Comments updatedComments = commentsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        updatedComments.setBody(commentsDto.getBody());
        updatedComments.setUpdatedAt(LocalDateTime.now());
        return ConvertUtils.convertCommentsToDto(updatedComments);
    }

    // 댓글 삭제, 부모 댓글이 삭제되면 계층형 댓글 구조에 문제가 생겨서 Activated를 false 처리
    public void delete(Long id) {
        Comments findComments = commentsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        findComments.setActivated(false);
    }

    // 부모가 없는 댓글 생성
    public CommentsDto nonReply(String username, CommentsDto commentsDto, Long postId) {
        commentsDto.setCreatedAt(LocalDateTime.now());
        commentsDto.setUpdatedAt(LocalDateTime.now());

        // 만들 댓글의 정보를 commments로 convert
        Comments createdComments = ConvertUtils.convertDtoToComments(commentsDto);
        createdComments.setActivated(true);

        // 만든 댓글의 연관관계를 설정
        createdComments.setUser(usersRepository.findByLoginId(username)); // 임시 메소드
        createdComments.setPost(postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
        // 영속화
        commentsRepository.save(createdComments);


        return ConvertUtils.convertCommentsToDto(commentsRepository.save(createdComments));
    }

    // 부모가 존재하는 댓글 설정
    public CommentsDto reply(Long postId, Long commentId, CommentsDto commentsDto, String username) {
        commentsDto.setCreatedAt(LocalDateTime.now());
        commentsDto.setUpdatedAt(LocalDateTime.now());

        // 만들 댓글의 정보를 comments로 convert
        Comments createdComments = ConvertUtils.convertDtoToComments(commentsDto);

        // 연관관계 설정, 부모가 존재하는 댓글이기 때문에 comments 도메인 내부의 연관관계도 설정
        createdComments.setUser(usersRepository.findByLoginId(username)); // 임시 메소드
        createdComments.setActivated(true);
        createdComments.setPost(postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
        createdComments.addReplyComment(commentsRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
        // 영속화
        commentsRepository.save(createdComments);

        return ConvertUtils.convertCommentsToDto(createdComments);
    }
}


