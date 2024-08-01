package com.springProject.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NickAndLoginId {
    private String loginId;
    private String nickname;
    private Boolean isEqual;
}
