package com.springProject.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsDto {

	private String title;
    private String body;
    private String category;
	private String location;
    private int star;
    private String hashtags;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private boolean isNotice;
}
