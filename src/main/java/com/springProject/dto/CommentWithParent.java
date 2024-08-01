package com.springProject.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentWithParent {

    private Long id;
    private Long parent;
    private String body;
    private String author;
    private String loginId;
    private boolean isActivated;
    private int depth;
    private LocalDateTime updatedAt;
    private List<CommentWithParent> children;
}
