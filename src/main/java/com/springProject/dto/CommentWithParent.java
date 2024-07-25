package com.springProject.dto;

import lombok.*;

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
    private List<CommentWithParent> children;
}
