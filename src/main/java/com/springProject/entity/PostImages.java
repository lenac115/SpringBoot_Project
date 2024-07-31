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
	private Long image_id;

	@ManyToOne
	@JoinColumn(name = "posts_id")
	private Posts post;

	@Column(name = "post_image")
	private String url;
}
