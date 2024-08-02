package com.springProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springProject.entity.Comments;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    @Query("select c from Comments c " +
            "left join fetch c.parent " +
            "where c.posts.id = :id " +
            "order by c.parent.id asc nulls first, c.createdAt asc")
    List<Comments> findByPostId(Long id);
}

	@Query("select c from Comments c " +
		"left join fetch c.parent " +
		"where c.posts.id = :id " +
		"order by c.parent.id asc nulls first, c.createdAt asc")
	List<Comments> findByPostId(Long id);
}