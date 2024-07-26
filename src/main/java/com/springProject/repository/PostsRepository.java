package com.springProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springProject.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {}
