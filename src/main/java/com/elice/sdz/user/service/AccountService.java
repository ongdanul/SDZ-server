package com.elice.sdz.user.service;

import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public List<Users> findByUserId(String userName, String contact) {
        return userRepository.findByUserNameAndContactAndSocialFalse(userName, contact);
    }

    @Transactional
    public String findUserPassword(String userId, String userName) {
        Users user = userRepository.findByUserIdAndUserName(userId, userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newPassword = createNewPassword();

        user.setUserPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setLoginLock(false);
        user.setLoginAttempts(0);
        user.setLastFailedLogin(null);

        userRepository.save(user);
        sendNewPasswordByMail(user.getEmail(), newPassword);

        log.info("Temporary password issued for user: {}", userId);
        return "Temporary password issuance has been completed.";
    }

    private String createNewPassword() {

        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder password  = new StringBuilder(8);
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 8; i++) {
            int index = secureRandom.nextInt(chars.length);
            char selectedChar = (index % 2 == 0) ? Character.toUpperCase(chars[index]) : chars[index];
            password.append(selectedChar);
        }
        return password.toString();
    }

    private void sendNewPasswordByMail(String toMailAddr, String newPassword) {
        try {
            final MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {

                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(toMailAddr);
                mimeMessageHelper.setSubject("[SDZ] 새 비밀번호 안내입니다.");
                mimeMessageHelper.setText("새 비밀번호 : " + newPassword, true);

            };

            javaMailSender.send(mimeMessagePreparator);
            log.info("New password sent to: {}", toMailAddr);
        } catch (Exception e) {
            log.error("Error occurred while sending email: {}", e.getMessage());
        }
    }
}
