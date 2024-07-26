package com.springProject.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostsDto {
    private Long user_id;

    private String title;
    private String body;
    private String category;
    private int star;
    private String hashtags;
    private Timestamp created_at;
    private Timestamp updated_at;

    private boolean isNotice;
}
