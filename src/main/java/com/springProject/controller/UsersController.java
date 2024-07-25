package com.springProject.controller;

import com.springProject.dto.MessageDto;
import com.springProject.dto.UsersDto;
import com.springProject.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    //회원가입 - 회원가입 페이지로 이동
    @GetMapping("/signupForm")
    public String createUsersForm() {
        return "login/signup";
    }

    //회원가입 - 유저 등록
    @PostMapping
    public String createUsers(@ModelAttribute UsersDto usersDto) {
        usersService.signUp(usersDto);
        return "redirect:/api/users/login";
    }

    //회원가입 - 아이디 중복 체크(fetch 비동기 처리)
    @GetMapping("/checkDuplicateId")
    @ResponseBody
    public ResponseEntity<Boolean> checkDuplicateId(@RequestParam String loginId) {
        boolean exists = usersService.isLoginIdDuplicate(loginId);
        return ResponseEntity.ok(exists);
    }

    //회원가입 - 닉네임 중복 체크(fetch 비동기 처리)
    @GetMapping("/checkDuplicateNickname")
    @ResponseBody
    public ResponseEntity<Boolean> checkDuplicateNickname(@RequestParam String nickname) {
        boolean exists = usersService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(exists);
    }

    //아이디 - 아이디 찾기 페이지로 이동
    @GetMapping("/findAccountForm")
    public String findAccountForm() {return "account/findAccount";}
    
    //아이디 - 사용자 유무 체크(fetch 비동기 처리)
    @PostMapping("/findAccount")
    @ResponseBody
    public ResponseEntity<Boolean> findAccount(@RequestParam String name, @RequestParam String email) {
        boolean found = usersService.isFindAccount(name, email);
        return ResponseEntity.ok(found);
    }
    
    //비밀번호 - 임시 비밀번호 발급 페이지로 이동
    @GetMapping("/findPasswordForm")
    public String findPasswordForm() {return "account/tempPassword";}

    //비밀번호 - 임시비밀번호 발급
    @PostMapping("/findPassword")
    @ResponseBody
    public ResponseEntity<Boolean> findPassword(@RequestParam String loginId, @RequestParam String email) {
        boolean found = usersService.isFindPassword(loginId, email);
        return ResponseEntity.ok(found);
    }

    //비밀번호 - 비밀번호 변경 페이지로 이동
    @GetMapping("/resetPasswordForm")
    public String resetPasswordForm() {return "account/resetPassword";}

    //비밀번호 - 비밀번호 변경
    @PostMapping("/resetPassword")
    @ResponseBody
    public ResponseEntity<MessageDto> resetPassword(@RequestParam String loginId, @RequestParam String password, @RequestParam String newPassword) {
        MessageDto message = usersService.resetPassword(loginId, password, newPassword);
        return ResponseEntity.ok(message);
    }

    //로그인 - 로그인 페이지로 이동
    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false) String error, @RequestParam(name = "expired", required = false) String expired,  Model model)
    {
        //사용자의 아이디 or 비밀번호가 다를 경우
        if(error != null) {
            model.addAttribute("error", true);
        }

        //중복 로그인 방지
        if(expired != null) {
            model.addAttribute("expired", true);
        }

        return "login/login";
    }

    //로그인 - 로그인 성공시 메인 페이지 이동
    @GetMapping("/main")
    public String mainPage() {return "login/main";}

    //회원탈퇴 - 회원탈퇴 페이지로 이동
    @GetMapping("/withdrawForm")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public String withdrawForm(){return "account/withdraw";}

    //회원탈퇴 - 회원탈퇴
    @PostMapping("/withdraw")
    @ResponseBody
    public ResponseEntity<MessageDto> withdraw(@RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
        //현재 로그인한 사용자의 정보를 가져온다.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginId = auth.getName(); //사용자 아이디

        MessageDto message = usersService.withdrawUser(loginId, password, request, response);
        return ResponseEntity.ok(message);
    }

    //권한 - 권한 테스트
    @GetMapping("/admin/test")
    public String adminPageTest() {return "login/admin";}

    //권한 - 권한에 따른 접근 불가 페이지로 이동
    @GetMapping("/accessDenied")
    public String accessDenied() {return "login/accessDenied";}
}
