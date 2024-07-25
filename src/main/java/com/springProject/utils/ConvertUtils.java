package com.springProject.utils;

import com.springProject.dto.CommentsDto;
import com.springProject.dto.PostsDto;
import com.springProject.dto.UsersDto;
import com.springProject.entity.Comments;
import com.springProject.entity.Posts;
import com.springProject.entity.Users;

public class ConvertUtils {

    public static CommentsDto convertCommentsToDto(Comments comments) {
        return CommentsDto.builder()
                .body(comments.getBody())
                .createdAt(comments.getCreatedAt())
                .updatedAt(comments.getUpdatedAt())
                .build();
    }

    public static Comments convertDtoToComments(CommentsDto commentsDto) {
        return Comments.builder()
                .body(commentsDto.getBody())
                .createdAt(commentsDto.getCreatedAt())
                .updatedAt(commentsDto.getUpdatedAt())
                .build();
    }

    public static Users convertDtoToUsers(UsersDto usersDto) {
        return Users.builder()
                .nickname(usersDto.getNickname())
                .auth(usersDto.getAuth())
                .loginId(usersDto.getLoginId())
                .email(usersDto.getEmail())
                .activated(usersDto.getActivated())
                .build();
    }

    public static UsersDto convertUsersToDto(Users users) {
        return UsersDto.builder()
                .nickname(users.getNickname())
                .auth(users.getAuth())
                .loginId(users.getLoginId())
                .email(users.getEmail())
                .activated(users.getActivated())
                .build();
    }

    public static PostsDto convertPostsToDto(Posts posts) {
        return PostsDto.builder()
                .title(posts.getTitle())
                .category(posts.getCategory())
                .body(posts.getBody())
                .star(posts.getStar())
                .hashtags(posts.getHashtags())
                .createdAt(posts.getCreated_at())
                .updatedAt(posts.getUpdated_at())
                .build();
    }

    public static Posts convertDtoToPosts(PostsDto postsDto) {
        return Posts.builder()
                .title(postsDto.getTitle())
                .category(postsDto.getCategory())
                .body(postsDto.getBody())
                .star(postsDto.getStar())
                .hashtags(postsDto.getHashtags())
                .created_at(postsDto.getCreatedAt())
                .updated_at(postsDto.getUpdatedAt())
                .build();
    }
}
