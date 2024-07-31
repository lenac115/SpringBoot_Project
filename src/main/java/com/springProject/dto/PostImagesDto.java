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
public class PostImagesDto {

	private long image_id;
	private Posts post_id;
	private String url;
}
