package com.springProject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.springProject.entity.Posts;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long>, PostsRepositoryCustom {

    @Query("select p from Posts p where p.isNotice = TRUE")
    List<Posts> findAllByNotice();

  	@Query("select p from Posts p where p.isNotice = true order by p.created_at DESC limit 5")
  	List<Posts> getNoticeFive();

    @Query("SELECT p FROM Posts p WHERE p.isNotice = true ORDER BY p.created_at DESC")
    List<Posts> getAllNotice();

    @Query("Select p FROM Posts p WHERE p.isNotice = false ORDER BY p.created_at DESC")
    List<Posts> getAllPosts();

    @Query("SELECT p FROM Posts p WHERE p.title LIKE %:title% AND p.isNotice = false")
    List<Posts> searchByTitleLike(String title);
}