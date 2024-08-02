package com.springProject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                        .requestMatchers("/", "/api/users/login", "/api/users/signup", "/api/users/**").permitAll() // 모든 사용자 접속 가능한 url(임시 설정)
                        .requestMatchers("forgot-password", "process_forgot_password").hasRole("user") // 권한이 'user' 인 경우만 접속 가능한 url(임시 설정)
                        .requestMatchers("/api/users/admin/**").hasRole("admin") // 권한이 'admin' 인 경우만 접속 가능한 url(임시 설정)
//                        .anyRequest().authenticated() // 인증 되지 않는 사용자일 경우 모든 요청을 Spring Security 에서 가로챔(설정한 url을 제외한 url은 이 설정을 적용할 예정)
                        .anyRequest().permitAll() //개발 중에만 모든 url 접속 허용(개발 완료 후 설정 막음(특정 url을 기준으로 설정할 예정))
                )
                .formLogin((form) -> form
                        .loginPage("/api/users/login").permitAll() // 로그인 페이지 경로 설정
                        .defaultSuccessUrl("/api/users/main", true) // 로그인 성공 후 이동할 경로(추후 이동할 경로 변경)
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
