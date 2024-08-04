package com.springProject.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrefersDto {

    private PostsDto postsDto;
    private UsersDto usersDto;
}
