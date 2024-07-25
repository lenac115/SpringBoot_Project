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
    private int star;
    private String body;
    private String hashtags;
    private String category;
    private String location;

    private Timestamp createdAt;
    private Timestamp updatedAt;

}
