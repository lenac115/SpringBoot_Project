package com.springProject.utils;

import com.springProject.entity.Posts;

import com.springProject.entity.Users;
import com.springProject.repository.PostsRepository;
import com.springProject.repository.UsersRepository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Aspect
@Component
public class TempPostDeleteAspect {

    private final PostsRepository postsRepository;

    private final UsersRepository usersRepository;

    public TempPostDeleteAspect(PostsRepository postsRepository, UsersRepository usersRepository) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
    }

    @Pointcut("execution(* com.springProject..*Controller.getPostsBySearchDataAndSortBy(..))")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    @Transactional
    public Object checkPostNull(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return joinPoint.proceed();
        }
        Users findUsers = usersRepository.findByLoginId(authentication.getName());
        List<Posts> findAll = postsRepository.findAllByUserId(findUsers.getId());

        findAll.removeIf(post -> {
            if ("임시".equals(post.getTitle())) {
                postsRepository.delete(post);
                return true;
            }
            return false;
        });

        findUsers.setPosts(findAll);

        return joinPoint.proceed();
    }
}