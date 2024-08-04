package com.springProject.dto;

import com.springProject.entity.Users;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDto {
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Users.UserAuth auth;
    private Boolean isActivated;
    private Long bannedUserId;
}
