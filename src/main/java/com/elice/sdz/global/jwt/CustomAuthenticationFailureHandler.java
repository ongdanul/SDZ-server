package com.elice.sdz.global.jwt;

import com.elice.sdz.global.exception.CustomOauth2Exception;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UserRepository userRepository;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // OAuth2 인증 실패인 경우, 커스텀 오류 메시지를 포함한 HTML로 응답
        if (exception instanceof CustomOauth2Exception) {
            response.setContentType("text/html;charset=UTF-8");
            String message = exception.getMessage();
            response.getWriter().write(
                    "<html><script>"
                            + "alert('" + message + "');"
                            + "window.location.href = '/user/login';"
                            + "</script></html>"
            );
            return;
        }

        // 일반 로그인 실패 처리
        String userId = request.getParameter("username");

        log.info("Test - onAuthenticationFailure userId : {}", userId);

        // 로그인 실패 처리: 실패 횟수 증가 및 잠금 여부 처리
        boolean isLocked = handleLoginFailure(userId);

        // 이미 잠금된 계정인 경우
        if (isLocked) {
            sendErrorResponse(response, HttpStatus.FORBIDDEN.value(), "LOGIN_LOCKED");
            return;
        }
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "LOGIN_FAILED");
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        JSONObject json = new JSONObject();
        json.put("message", message);

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }

    public boolean handleLoginFailure(String userId) {
        final int MAX_ATTEMPTS = 5;

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // 계정이 이미 잠금 상태인 경우
        if (user.isLoginLock()) {
            log.error("Locked account: {} ", userId);
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
            log.error("Error occurred while updating user login status: {}", user.getUserId(), e);
            throw new RuntimeException("An error occurred while updating user status.");
        }
    }
}
