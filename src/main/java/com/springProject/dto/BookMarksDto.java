package com.springProject.dto;

import com.springProject.entity.Posts;
import com.springProject.entity.Users;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookMarksDto {

    private Long id;
    private PostsDto postsDto;
    private UsersDto usersDto;

}
