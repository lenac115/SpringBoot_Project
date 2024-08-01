package com.springProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springProject.entity.Users;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByLoginId(String loginId); //Security 사용
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    Users findByNameAndEmail(String name, String email);
    Users findByLoginIdAndEmail(String loginId, String email);

    @Query("select u from Users u where u.loginId = :loginId")
    Optional<Users> findOptionalByLoginId(String loginId);
}
