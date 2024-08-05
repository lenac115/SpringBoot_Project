package com.springProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springProject.entity.Prefers;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrefersRepository extends JpaRepository<Prefers, Long> {

    @Query("select count(p) from Prefers p where p.posts.id = :postId")
    Integer countById(Long postId);

    @Query("select p from Prefers p where p.users.id = :userId")
    List<Prefers> findAllByUserId(Long userId);

    @Query("select p from Prefers p where p.posts.id = :postId and p.users.id = :userId")
    Optional<Prefers> findByPostId(Long postId, Long userId);
}
