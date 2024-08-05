package com.springProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "bookmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookMarks {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookmark_id;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posts_id")
	private Posts posts;


}
