package com.springProject.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for {@link com.springProject.entity.DeletePosts} */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeletePostsDto {

  private UsersDto users;
  private String title;
  private String reason;
  private LocalDateTime deleted_at;
}
