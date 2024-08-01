package com.springProject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "login_id")
	private String loginId;

	@Column
	private String password;

	@Column
	private String name;

	@Column
	private String nickname;

	@Column
	private String email;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(columnDefinition = "boolean default true")
	private Boolean isActivated = true;

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Comments> comments = new ArrayList<>();

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Posts> posts = new ArrayList<>();

	@OneToOne
	@JoinColumn(name = "banned_id")
	private BannedUser bannedUser;

	@Enumerated(EnumType.STRING)
	private UserAuth auth = UserAuth.user;

	public enum UserAuth {
		user, admin, stop
	}
}
