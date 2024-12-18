package com.elice.sdz.global.jwt;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UserRepository userRepository;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        String userId = request.getParameter("username");
        log.info("Test - onAuthenticationFailure userId : {}", userId);

        // 로그인 실패 처리: 실패 횟수 증가 및 잠금 여부 처리
        boolean isLocked = handleLoginFailure(userId);

        // 이미 잠금된 계정인 경우
        if (isLocked) {
            throw new CustomException(ErrorCode.LOGIN_LOCKED);
        }
        throw new CustomException(ErrorCode.LOGIN_FAILED);
    }

    public boolean handleLoginFailure(String userId) {
        final int MAX_ATTEMPTS = 5;

        Users user = userRepository.findByEmail(userId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND));

        // 계정이 이미 잠금 상태인 경우
        if (user.isLoginLock()) {
            log.error("로그인 잠금된 아이디입니다.: {} ", userId);
            return true;
        }

        // 현재 실패 횟수로 계정 잠금 여부 검증 (MAX_ATTEMPTS 이상이면 잠금)
        if (user.getLoginAttempts() >= MAX_ATTEMPTS) {
            user.setLoginLock(true);
            updateUserState(user);
            return true;
        }

        // 실패 횟수 증가
        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts); // 로그인 실패 횟수 저장
        user.setLastFailedLogin(Instant.now()); // 마지막 실패 시간 갱신

        // 실패 횟수가 MAX_ATTEMPTS에 도달하면 잠금 처리
        if (attempts >= MAX_ATTEMPTS) {
            user.setLoginLock(true);
            updateUserState(user);
            return true;
        }

        updateUserState(user);
        return false;
    }

    private void updateUserState(Users user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("로그인 가능 상태 여부 수정 중 오류가 발생하였습니다.: {}", user.getEmail(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
