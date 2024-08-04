package com.springProject.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delete_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletePosts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delete_id")
	private long id;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String reason;

	@Column(nullable = false)
	private LocalDateTime deleted_at;
}
