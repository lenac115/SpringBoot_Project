package com.springProject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImages {
	@Id @Column(name = "post_images_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String originFilename;

	private String storeFilename;

	private String filePath;

	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "posts_id")
	private Posts posts;
}
