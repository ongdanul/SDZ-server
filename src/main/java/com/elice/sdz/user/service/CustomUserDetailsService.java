package com.elice.sdz.user.service;

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

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("회원이 존재하지 않습니다.: {}", userId);
                    return new UsernameNotFoundException("회원이 존재하지 않습니다.");
                });
        log.info("Test - CustomUserDetailsService : userId: {}", userId);
        log.info("Test - CustomUserDetailsService : userId: {}, userPassword: {}", user.getUserId(), user.getUserPassword());

        return new CustomUserDetails(user);
    }
}