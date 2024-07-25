package com.springProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
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

	@Enumerated(EnumType.STRING)
	private UserAuth auth = UserAuth.user;

	public enum UserAuth {
		user, admin, stop
	}
}
