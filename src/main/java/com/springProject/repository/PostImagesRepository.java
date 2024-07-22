package com.springProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springProject.entity.PostImages;

@Repository
public interface PostImagesRepository extends JpaRepository<PostImages, Long> {}
