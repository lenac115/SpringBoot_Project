package com.springProject.dto;

import lombok.*;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

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
