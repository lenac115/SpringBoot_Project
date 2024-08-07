package com.springProject.utils;

import com.springProject.entity.Users;
import com.springProject.repository.BannedUserRepository;
import com.springProject.repository.UsersRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class UserAccessAspect {

    private final UsersRepository usersRepository;
    private final BannedUserRepository bannedUserRepository;

    public UserAccessAspect(UsersRepository usersRepository, BannedUserRepository bannedUserRepository) {
        this.usersRepository = usersRepository;
        this.bannedUserRepository = bannedUserRepository;
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object checkUserAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return joinPoint.proceed();
        }

        Users findUser = usersRepository.findOptionalByLoginId(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if (findUser.getBannedUser() == null) {
            return joinPoint.proceed();
        }

        if (LocalDateTime.now().isAfter(findUser.getBannedUser().getBannedDate())) {
            findUser.setIsActivated(true);
            findUser.setBannedUser(null);
            bannedUserRepository.deleteByUsersId(findUser.getId());
            return joinPoint.proceed();
        }

        throw new AccessDeniedException("정지된 사용자입니다.");
    }
}