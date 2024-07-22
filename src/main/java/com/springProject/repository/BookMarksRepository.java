package com.springProject.repository;

import java.awt.print.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springProject.entity.BookMarks;

@Repository
public interface BookMarksRepository extends JpaRepository<BookMarks, Long> {

}

