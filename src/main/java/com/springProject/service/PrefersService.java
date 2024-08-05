package com.springProject.service;

import com.springProject.dto.PrefersDto;
import com.springProject.entity.BookMarks;
import com.springProject.entity.Posts;
import com.springProject.entity.Prefers;
import com.springProject.entity.Users;
import com.springProject.repository.PostsRepository;
import com.springProject.repository.PrefersRepository;
import com.springProject.repository.UsersRepository;
import com.springProject.utils.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PrefersService {

    private final PrefersRepository prefersRepository;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;


    public Integer countById(Long postId) {
        return prefersRepository.countById(postId);
    }

    public List<PrefersDto> getAllPrefers(Long userId) {
        return prefersRepository.findAllByUserId(userId).stream()
                .map(ConvertUtils::convertPrefersToDto).toList();
    }

    public PrefersDto save(Long postId, String username) {
        Users findUsers = usersRepository.findOptionalByLoginId(username).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        Posts findPosts = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        Prefers prefers = prefersRepository.save(Prefers.builder()
                .posts(findPosts)
                .users(findUsers)
                .build());
        findUsers.getPrefers().add(prefers);
        findPosts.getPrefers().add(prefers);

        return ConvertUtils.convertPrefersToDto(prefers);
    }

    public void delete(Long postId, String username) {
        Users findUser = usersRepository.findByLoginId(username);
        Prefers findPrefers = prefersRepository.findByPostId(postId, findUser.getId()).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        if(!Objects.equals(findPrefers.getUsers().getLoginId(), username))
            throw new AccessDeniedException("잘못된 접근입니다.");

        prefersRepository.delete(findPrefers);
    }
}
