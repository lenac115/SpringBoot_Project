package com.springProject.service;

import com.springProject.dto.BookMarksDto;
import com.springProject.entity.BookMarks;
import com.springProject.entity.Users;
import com.springProject.repository.BookMarksRepository;
import com.springProject.repository.PostsRepository;
import com.springProject.repository.UsersRepository;
import com.springProject.utils.ConvertUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookMarksService {

    private final BookMarksRepository bookMarksRepository;
    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<BookMarksDto> findAllById(Long userId) {
        return Optional.ofNullable(bookMarksRepository.findAllByUserId(userId)).orElseGet(Collections::emptyList)
                .stream().map(ConvertUtils::convertBookMarksToDto).toList();
    }

    public BookMarksDto save(Long postId, String username) {

        BookMarks newBookMarks = new BookMarks();
        newBookMarks.setPosts(postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
        newBookMarks.setUsers(usersRepository.findByLoginId(username));

        return ConvertUtils.convertBookMarksToDto(bookMarksRepository.save(newBookMarks));
    }

    public void delete(Long postId, String username) {
        Users findUser = usersRepository.findByLoginId(username);
        BookMarks findMarks = bookMarksRepository.findByPostId(postId, findUser.getId()).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        if(!Objects.equals(findMarks.getUsers().getLoginId(), username))
            throw new AccessDeniedException("잘못된 접근입니다.");

        bookMarksRepository.delete(findMarks);
    }
}
