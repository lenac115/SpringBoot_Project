package com.springProject.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Posts {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long post_id;

	@Column
	private Long user_id;

	@Column
	private String title;

	@Column
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

	@Column
	private boolean isNotice;
}
