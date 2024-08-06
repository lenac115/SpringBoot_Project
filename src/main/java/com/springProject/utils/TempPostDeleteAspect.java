package com.springProject.utils;

import com.springProject.entity.Posts;
import com.springProject.repository.PostsRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class TempPostDeleteAspect {

    private final PostsRepository postsRepository;

    public TempPostDeleteAspect(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object checkPostNull(ProceedingJoinPoint joinPoint) throws Throwable {
        List<Posts> findAll = postsRepository.findAll();

        for (Posts posts : findAll) {
            if(posts.getTitle().equals("임시")) {
                postsRepository.delete(posts);
            }
        }

        return joinPoint.proceed();
    }
}