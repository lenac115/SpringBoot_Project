package com.springProject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springProject.SearchData;
import com.springProject.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long>, PostsRepositoryCustom {
  @Query("select p from Posts p where p.isNotice = true order by p.created_at DESC limit 5")
  List<Posts> getNoticeFive();
}

