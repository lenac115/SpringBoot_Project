package com.springProject.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for {@link com.springProject.entity.StopUser} */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopUserDto {
  private Long id;
  private UsersDto user;
  private String reason;
  private Integer duration;
}
