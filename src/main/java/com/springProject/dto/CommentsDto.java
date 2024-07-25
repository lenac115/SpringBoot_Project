package com.springProject.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class CommentsDto {

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
