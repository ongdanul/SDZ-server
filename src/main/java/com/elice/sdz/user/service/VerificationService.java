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


    public Boolean isUserEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    public Boolean isNicknameExists(String nickname) {

        return userRepository.existsByNickname(nickname);
    }

    public boolean existsByEmailAndUserName(String email, String userName) {
        return userRepository.existsByEmailAndUserName(email, userName);
    }

    public boolean checkPassword(String email, String inputPassword) {
        String storedPassword = userRepository.findPasswordByEmail(email);
        return bCryptPasswordEncoder.matches(inputPassword, storedPassword);
    }
}
