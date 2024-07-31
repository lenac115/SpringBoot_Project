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
public class PrefersDto {

	private long id;
	private Users user;
	private Posts post;
}
