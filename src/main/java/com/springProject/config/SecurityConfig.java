package com.springProject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final String rememberKey;

    public SecurityConfig(@Value("${spring.security.remember-me.key}") String rememberKey) {
        this.rememberKey = rememberKey;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(HttpMethod.GET, "/api/users/login").permitAll() //로그인
                .requestMatchers(HttpMethod.GET, "/api/users/signupForm").permitAll() //회원가입
                .requestMatchers(HttpMethod.GET, "/api/users/checkDuplicateId").permitAll() //아이디중복
                .requestMatchers(HttpMethod.GET, "/api/users/checkDuplicateNickname").permitAll() //닉네임중복
                .requestMatchers(HttpMethod.GET, "/api/users/checkDuplicateEmail").permitAll() //이메일중복
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll() //회원가입
                .requestMatchers(HttpMethod.GET, "/api/users/findAccountForm").permitAll() //아이디찾기
                .requestMatchers(HttpMethod.POST, "/api/users/findAccount").permitAll() //아이디찾기
                .requestMatchers(HttpMethod.GET, "/api/users/findPasswordForm").permitAll() //비밀번호찾기
                .requestMatchers(HttpMethod.POST, "/api/users/findPassword").permitAll() //비밀번호찾기
                .requestMatchers(HttpMethod.GET, "/api/users/resetPasswordForm").permitAll() //비밀번호변경
                .requestMatchers(HttpMethod.POST, "/api/users/resetPassword").permitAll() //비밀번호변경 추후 비로그인 시 보여도 되는 페이지들(메인페이지,게시판 추가 예정)
                .requestMatchers(HttpMethod.GET, "/api/posts/search").permitAll()
                .requestMatchers("/css/**", "/js/**").permitAll() //정적파일
                .anyRequest().authenticated() // 인증 되지 않는 사용자일 경우 모든 요청을 Spring Security 에서 가로챔(설정한 url을 제외한 url은 이 설정을 적용할 예정)
            )
            .formLogin((form) -> form
                .loginPage("/api/users/login").permitAll() // 로그인 페이지 경로 설정
                .successHandler((request, response, authentication) -> { // 로그인 성공 시 권한에 따른 리다이렉트 처리
                    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                    if(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"))) {
                        response.sendRedirect("/api/posts/search");
                    } else if(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_user"))) {
                        response.sendRedirect("/api/posts/search");
                    } else if(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_stop"))) {
                        response.sendRedirect("/api/posts/search"); // 공지사항 페이지로 변경해야됨
                    } else {
                        response.sendRedirect("/api/users/login");
                    }
                })
                .failureUrl("/api/users/login?error=true") // 로그인 실패 시
            )
            .logout((logout) -> logout
                .logoutUrl("/logout") //로그아웃 경로
                .logoutSuccessUrl("/api/users/login") //로그아웃 시 이동할 경로
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // 쿠키 삭제
                .permitAll()
            )
            .exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendRedirect("/api/users/login?expired=true") // 모든 인증 되지 않는 요청(세션 포함)을 로그인 페이지로 리다이렉트
                )
                .accessDeniedPage("/api/users/accessDenied")) // 권한에 따른 접근 불가 페이지 설정
            .rememberMe((rememberMe) -> rememberMe
                .key(rememberKey) // remember-me key 설정
                .tokenValiditySeconds(3600)) // remember-me 유지 시간 설정
            .sessionManagement((sessionManagement) -> sessionManagement
                .maximumSessions(1) // 최대 1개 세션 허용(중복 로그인 방지)
                .maxSessionsPreventsLogin(false) // 다른 장치에서 로그인 시 기존 세션 만료하지 않음(로그아웃 후 다시 로그인 처리가 되기 위해)
                .expiredUrl("/api/users/login?expired=true"))// 세션이 만료된 경우 이동할 경로
            .csrf((csrf) -> csrf
                .ignoringRequestMatchers("/api/**")
            );

        return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}