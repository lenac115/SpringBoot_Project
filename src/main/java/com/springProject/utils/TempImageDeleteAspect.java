package com.springProject.utils;

import com.springProject.entity.PostImages;
import com.springProject.repository.PostImagesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class TempImageDeleteAspect {

    private final PostImagesRepository postImagesRepository;

    @Pointcut("execution(public * com.springProject.service.PostImagesService.attachImagesToPost(..))")
    public void serviceMethod() {
    }

    @After("serviceMethod()")
    public void tempImageDelete() throws Throwable {
        List<PostImages> postImagesList = postImagesRepository.findAll();
        for(PostImages postImages : postImagesList) {
            if (postImages.getPosts() == null) {
                postImagesRepository.delete(postImages);
            }
        }
    }
}
