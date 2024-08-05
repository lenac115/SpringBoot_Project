package com.springProject.service;

import com.springProject.dto.BannedDateReasonForm;
import com.springProject.dto.MessageDto;
import com.springProject.dto.UsersDto;
import com.springProject.entity.BannedUser;
import com.springProject.entity.Users;
import com.springProject.repository.BannedUserRepository;
import com.springProject.repository.UsersRepository;
import com.springProject.utils.ConvertUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsersService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsersRepository usersRepository;
    private final BannedUserRepository bannedUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder,
                        EmailService emailService, BannedUserRepository bannedUserRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.bannedUserRepository = bannedUserRepository;
    }

    @Transactional
    public void signUp(UsersDto usersDto) {
        Users user = convertToEntity(usersDto);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean isLoginIdDuplicate(String loginId) {
        return usersRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameDuplicate(String nickname) {
        return usersRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public boolean isFindAccount(String name, String email) {
        Users user = usersRepository.findByNameAndEmail(name, email);

        if (user != null) {
            emailService.sendUserLoginIdEmail(email, user.getLoginId());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean isFindPassword(String loginId, String email) {
        Users user = usersRepository.findByLoginIdAndEmail(loginId, email);

        if (user != null) {
            //임시 비밀번호 생성
            String tempPassword = createRandomPassword();
            //임시 비밀번호 저장
            user.setPassword(passwordEncoder.encode(tempPassword));
            user.setUpdatedAt(LocalDateTime.now());
            usersRepository.save(user);
            //임시 비밀번호 이메일 발송
            emailService.sendTempPasswordEmail(email, tempPassword);
            return true;
        }
        return false;
    }

    @Transactional
    public MessageDto resetPassword(String loginId, String password, String newPassword) {
        Users user = usersRepository.findByLoginId(loginId);

        //입력한 loginId가 DB에 존재하지 않는 경우
        if (user == null) {
            return new MessageDto("idNotFound", "일치하는 사용자가 없습니다.");
        }

        //입력한 비밀번호와 DB의 비밀번호가 다를 경우
        if (!passwordEncoder.matches(password, user.getPassword())) { // 입력한 비밀번호, DB 비밀번호 검증 절차
            return new MessageDto("passwordDiff", "입력하신 비밀번호가 기존 비밀번호와 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        try {
            usersRepository.save(user);
            return new MessageDto("success", "비밀번호를 변경하였습니다.");
        } catch (Exception e) {
            return new MessageDto("error", "비밀번호 변경 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public MessageDto withdrawUser(String loginId, String password, HttpServletRequest request, HttpServletResponse response) {
        Users user = usersRepository.findByLoginId(loginId);
        //DB에 일치하는 사용자 정보가 없을 경우
        if (user == null) {
            return new MessageDto("idNotFound", "일치하는 사용자가 없습니다.");
        }

        //입력한 비밀번호와 DB에 저장된 비밀번호가 일치하지 않을 경우
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new MessageDto("passwordDiff", "입력하신 비밀번호가 기존 비밀번호와 일치하지 않습니다.");
        }

        try {
            usersRepository.delete(user);

            //사용자 삭제 확인
            if (usersRepository.findByLoginId(loginId) != null) {
                return new MessageDto("error", "회원 탈퇴에 실패하였습니다.");
            }

            //로그아웃 처리
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            //세션 무효화
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            //보안 컨텍스트 클리어
            SecurityContextHolder.clearContext();
            //Remember-me 쿠키에서 제거
            deleteCookie(request, response, "remember-me");

            return new MessageDto("success", "정상적으로 회원 탈퇴되었습니다.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new MessageDto("error", "회원 탈퇴 중 오류가 발생하였습니다.");
        }
    }

    //DTO -> Entity
    public Users convertToEntity(UsersDto userDto) {
        Users user = new Users();
        user.setLoginId(userDto.getLoginId());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setName(userDto.getName());
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActivated(userDto.getIsActivated());
        return user;
    }

    //임시 비밀번호 생성(8자리)
    public String createRandomPassword() {
        return RANDOM.ints(8, 0, CHARS.length())
                .mapToObj(i -> String.valueOf(CHARS.charAt(i)))
                .collect(Collectors.joining());
    }

    //Spring Security Remeber-me 제거
    private void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath() + "/");
        response.addCookie(cookie);
    }


    @Transactional
    public UsersDto unActivate(Long userId, BannedDateReasonForm bannedForm) {
        isBanned();
        Users findUsers = usersRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));
        findUsers.setIsActivated(false);

        BannedUser user = BannedUser.builder()
                .users(findUsers)
                .bannedDate(bannedForm.getBannedDate())
                .bannedReason(bannedForm.getBannedReason())
                .bannedLoginId(bannedForm.getBannedLoginId())
                .build();

        findUsers.setBannedUser(user);
        bannedUserRepository.save(user);

        return ConvertUtils.convertUsersToDto(findUsers);
    }

    @Transactional
    public UsersDto activate(Long userId) {
        isBanned();
        Users findUsers = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        //유저가 정지 이력이 있는지 확인(그래야 정지 -> 활동으로 변경)
        if (findUsers.getBannedUser() != null) {
            findUsers.setBannedUser(null);
        }
        
        //정지 테이블의 로그는 그대로 남기고, 유저 테이블의 컬럼 값만 업데이트(banned_id)
        findUsers.setIsActivated(true);
        usersRepository.save(findUsers);
        return ConvertUtils.convertUsersToDto(findUsers);
    }

    @Transactional(readOnly = true)
    public List<UsersDto> getAllUsers() {
        isBanned();
        return Optional.ofNullable(usersRepository.findAll()).orElse(Collections.emptyList())
                .stream().map(ConvertUtils::convertUsersToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsersDto getUsers(Long id) {
        isBanned();
        return ConvertUtils.convertUsersToDto(usersRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다.")));
    }

    private void isBanned() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        Users findUser = usersRepository.findOptionalByLoginId(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("잘못된 ID 입니다."));

        if (findUser.getBannedUser() == null)
            return;
        if (LocalDateTime.now().isAfter(findUser.getBannedUser().getBannedDate())) {
            findUser.setIsActivated(true);
            bannedUserRepository.deleteByUsersId(findUser.getId());
            return;
        }

        throw new AccessDeniedException("정지된 사용자입니다.");
    }

    @Transactional(readOnly = true)
    public UsersDto getSearchUser(String userId) {
        return ConvertUtils.convertUsersToDto(usersRepository.findOptionalByLoginId(userId).orElseThrow(() -> new NoSuchElementException("User not found with userId: " + userId)));
    }
}