package com.springProject.repository;

import com.springProject.entity.BannedUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedUserRepository extends JpaRepository<BannedUser, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void deleteByUsersId(Long id);
}
