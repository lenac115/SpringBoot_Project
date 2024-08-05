package com.springProject.dto;

import com.springProject.entity.Posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImagesDto {

    private Long image_id;
  
    private Long post_id;

    private String originFilename;

    private String storeFilename;

    private String filePath;

    private LocalDateTime createdAt;

}
