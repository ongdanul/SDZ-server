package com.elice.sdz.user.service;

import com.elice.sdz.global.jwt.JWTUtil;
import com.elice.sdz.user.dto.CustomUserDetails;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userRepository.findByUserId(username)
                .orElseThrow(() -> {
                    log.error("User not found with userId: {}", username);
                    return new UsernameNotFoundException("User not found");
                });
        log.info("Test - CustomUserDetailsService : userName: {}", username);
        log.info("Test - CustomUserDetailsService : user: {}", user);
        log.info("Test - CustomUserDetailsService : userId: {}, userPassword: {}", user.getUserId(), user.getUserPassword());

        return new CustomUserDetails(user);
    }
}