package com.springProject.repository;

import com.springProject.entity.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.springProject.entity.Posts;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long>, PostsRepositoryCustom {

    @Query("select p from Posts p where p.isNotice = TRUE")
    List<Posts> findAllByNotice();

    @Query("select p from PostImages p where p.storeFilename = :filename")
    PostImages findByStoreFilename(String filename);
}