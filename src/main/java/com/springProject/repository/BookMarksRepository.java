package com.springProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springProject.entity.BookMarks;

@Repository
public interface BookMarksRepository extends JpaRepository<BookMarks, Long> {

    @Query("select b from BookMarks b where b.users.id = :userId")
    List<BookMarks> findAllByUserId(Long userId);
}

