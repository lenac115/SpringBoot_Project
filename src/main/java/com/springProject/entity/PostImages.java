package com.springProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "post_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostImages {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(nullable = false)
	private Long image_id;

	@Column(nullable = false)
	private Long post_id;

	@Column(nullable = false)
	private Long post_image;

	@ManyToOne
	@JoinColumn(name = "POSTS_ID")
	private Posts posts;
}
