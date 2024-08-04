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
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY)
	private Posts posts;
}
