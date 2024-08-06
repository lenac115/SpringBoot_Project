package com.springProject.repository;

import com.springProject.dto.PostImagesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springProject.entity.PostImages;

import java.util.List;

@Repository
public interface PostImagesRepository extends JpaRepository<PostImages, Long> {

    @Query("select p from PostImages p where p.posts.id = :postId")
    List<PostImages> findAllById(Long postId);
}
