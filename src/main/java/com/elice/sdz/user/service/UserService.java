package com.elice.sdz.user.service;

import com.elice.sdz.global.config.CookieUtils;
import com.elice.sdz.user.dto.UpdateLocalDTO;
import com.elice.sdz.user.dto.UserDetailDTO;
import com.elice.sdz.user.dto.SignUpDTO;
import com.elice.sdz.user.dto.UpdateSocialDTO;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.RefreshRepository;
import com.elice.sdz.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public boolean signUpProcess(@Valid SignUpDTO signUpDTO) {
        final int MAX_USER_ACCOUNTS = 3;

        long countUserIds = countUserIds(signUpDTO.getUserName(), signUpDTO.getContact());
        if (countUserIds >= MAX_USER_ACCOUNTS) { //최대 가입 계정 수 3개 제한
            log.error("sign-up limit reached: userName={} contact={}", signUpDTO.getUserName(), signUpDTO.getContact());
            throw new RuntimeException("The maximum number of available IDs has been exceeded.");
        }

        signUpDTO.setUserPassword(bCryptPasswordEncoder.encode(signUpDTO.getUserPassword()));
        Users user = new Users().signUpToEntity(signUpDTO);

        try {
            userRepository.save(user);
            log.info("User signed up successfully: {}", user.getUserName());
            return true;
        } catch (Exception e) {
            log.error("Error occurred during the sign-up process: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred during the sign-up process.");
        }
    }

    public long countUserIds(String userName, String contact) {
        return userRepository.countByUserNameAndContact(userName, contact);
    }

    public UserDetailDTO findByUserId(String userId) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserDetailDTO.builder()
                .userId(user.getUserId())
                .userPassword(user.getUserPassword())
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .contact(user.getContact())
                .email(user.getEmail())
                .social(user.isSocial())
                .profileUrl(user.getProfileUrl())
                .build();
    }

    @Transactional
    public void updateByLocalUser(UpdateLocalDTO updateLocalDTO) {
        userRepository.findByUserId(updateLocalDTO.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String userPassword = updateLocalDTO.getUserPassword();
        String encodedPassword = !StringUtils.hasText(userPassword) ?
                updateLocalDTO.getUserPassword() : bCryptPasswordEncoder.encode(userPassword);

        Users updatedUser = Users.builder()
                .userId(updateLocalDTO.getUserId())
                .userPassword(encodedPassword)
                .userName(updateLocalDTO.getUserName())
                .nickname(updateLocalDTO.getNickname())
                .contact(updateLocalDTO.getContact())
                .email(updateLocalDTO.getEmail())
                .build();

        userRepository.save(updatedUser);
    }

    @Transactional
    public void updateBySocialUser(UpdateSocialDTO updateSocialDTO) {
        userRepository.findByUserId(updateSocialDTO.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Users updatedUser = Users.builder()
                .userId(updateSocialDTO.getUserId())
                .userName(updateSocialDTO.getUserName())
                .nickname(updateSocialDTO.getNickname())
                .contact(updateSocialDTO.getContact())
                .email(updateSocialDTO.getEmail())
                .build();

        userRepository.save(updatedUser);
    }

    //TODO : 첨부파일 기능 완성 이후에 프로필 사진 변경 기능 연결 할 것.
//    public void updateByProfileImage(String userId, MultipartFile file) throws IOException {
//        userRepository.findByUserId(socialUserDTO.getUserId())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        Users updatedUser = Users.builder()
//                .userId(socialUserDTO.getUserId())
//                .profileUrl(socialUserDTO.getProfileUrl())
//                .build();
//
//        userRepository.save(updatedUser);
//    }

    @Transactional
    public void deleteByUser(HttpServletResponse response, String userId) {
        userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.deleteById(userId);
        refreshRepository.deleteAllByUserId(userId);
        CookieUtils.deleteCookie(response, "refresh");
    }
}
