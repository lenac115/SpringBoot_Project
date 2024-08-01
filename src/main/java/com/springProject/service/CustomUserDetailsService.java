package com.springProject.service;

import com.springProject.entity.Users;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByLoginId(username);
        if(user == null) {
            throw new UsernameNotFoundException("loginId 에서 사용자를 찾을 수 없습니다. loginId : " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getLoginId(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getAuth().name())));
    }
}
