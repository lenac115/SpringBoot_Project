package com.springProject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prefers")
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prefers {
	@Id @Column(name = "prefers_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long prefer_id;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	// 외래키
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posts_id")
	private Posts posts;
}
