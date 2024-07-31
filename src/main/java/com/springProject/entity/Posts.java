package com.springProject.entity;

import java.sql.Timestamp;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

    @Id @Column(name = "posts_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column
    private String category;

    @Column
    private String location;

    @Column
    private Integer star;

    @Column
    private String hashtags;

    @Column
    private Timestamp created_at;

    @Column
    private Timestamp updated_at;

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL)
    private List<Comments> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @Column(name = "is_notice")
    private boolean isNotice;

}
