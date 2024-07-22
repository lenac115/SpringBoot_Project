package com.springProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springProject.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {}
