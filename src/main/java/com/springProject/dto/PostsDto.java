package com.springProject.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsDto {

	private Long id;
	private String title;
    private String body;
    private String category;
	private String location;
    private Integer star;
    private String hashtags;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private boolean isNotice;

    private UsersDto usersDto;
    private String presentId;

    private boolean isBookmark = false;
    private boolean isPrefers = false;
}
