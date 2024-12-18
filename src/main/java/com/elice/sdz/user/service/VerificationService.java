package com.elice.sdz.user.service;

import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public Boolean isUserIdExists(String userId) {
        return userRepository.existsByEmail(userId);
    }
    public Boolean isNicknameExists(String nickname) {

        return userRepository.existsByNickname(nickname);
    }

    public boolean existsByUserIdAndUserName(String userId, String userName) {
        return userRepository.existsByEmailAndUserName(userId, userName);
    }

    public boolean checkPassword(String userId, String inputPassword) {
        String storedPassword = userRepository.findPasswordByEmail(userId);
        return bCryptPasswordEncoder.matches(inputPassword, storedPassword);
    }
}
