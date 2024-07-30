package com.springProject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
	// 기본 키
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comments_id")
	private Long id;

	// 내용
	@Column(columnDefinition = "TEXT", nullable = false)
	private String body;

	// 삭제 여부
	@ColumnDefault("true")
	@Column(nullable = false)
	private boolean isActivated;

	// 댓글 깊이
	@ColumnDefault("0")
	@Column(nullable = false)
	private int depth;

	// 댓글의 부모 표기
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Comments parent;

	// 대댓글
	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
	private List<Comments> children = new ArrayList<>();

	// 생성 시간, 생성때 한번 설정된 이후로 재설정 x
	@Column(nullable = false)
	private LocalDateTime createdAt;

	// 갱신 시간, 갱신때마다 재설정
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posts_id")
	private Posts posts;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	//포스트 연관관계 지정
	public void setPost(Posts post) {
		this.posts = post;
		post.getComments().add(this);
	}

	//유저 연관관계 지정
	public void setUser(Users users) {
		this.users = users;
		users.getComments().add(this);
	}

	// 대댓글 연관관계
	public void addReplyComment(Comments parent) {
		this.parent = parent;
		this.depth = parent.getDepth() + 1;
		if(this.parent.children == null) {
			this.parent.children = new ArrayList<>();
		}
		this.parent.children.add(this);
	}
	// 기본 키
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comments_id")
	private Long id;

	// 내용
	@Column(columnDefinition = "TEXT", nullable = false)
	private String body;

	// 삭제 여부
	@ColumnDefault("true")
	@Column(nullable = false, name = "activated")
	private boolean activated;

	// 댓글 깊이
	@ColumnDefault("0")
	@Column(nullable = false, name = "depth")
	private int depth;

	// 댓글의 부모 표기
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Comments parent;

	// 대댓글
	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
	private List<Comments> children = new ArrayList<>();

	// 생성 시간, 생성때 한번 설정된 이후로 재설정 x
	@Column(nullable = false, name = "created_at")
	private LocalDateTime createdAt;

	// 갱신 시간, 갱신때마다 재설정
	@Column(nullable = false, name = "updated_at")
	private LocalDateTime updatedAt;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posts_id")
	private Posts posts;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	//포스트 연관관계 지정
	public void setPost(Posts post) {
		this.posts = post;
		post.getComments().add(this);
	}

	//유저 연관관계 지정
	public void setUser(Users users) {
		this.users = users;
		users.getComments().add(this);
	}

	// 대댓글 연관관계
	public void addReplyComment(Comments parent) {
		this.parent = parent;
		this.depth = parent.getDepth() + 1;
		if(this.parent.children == null) {
			this.parent.children = new ArrayList<>();
		}
		this.parent.children.add(this);
	}
}
