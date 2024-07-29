package com.springProject.repository;

import com.springProject.entity.BannedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {

    void deleteByUsersId(Long id);
}
