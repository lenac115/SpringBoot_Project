package com.springProject.dto;

import com.springProject.entity.Posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostsDto {
	public static PostsDto convertToDto(Posts posts) {
		return null;
	}
}
