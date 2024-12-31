package com.elice.sdz.global.jwt;

import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.request.LoginRequest;
import com.elice.sdz.user.dto.response.LoginResponse;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UserRepository userRepository;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        LoginRequest loginRequest = (LoginRequest) request.getAttribute("loginRequest");
        String email = loginRequest.getEmail();

        // 로그인 실패 처리: 실패 횟수 증가 및 잠금 여부 처리
        handleLoginFailure(response, email);
    }

    public void handleLoginFailure(HttpServletResponse response, String email) throws  IOException {
        final int MAX_ATTEMPTS = 5;

        Optional<Users> optionalUser = userRepository.findById(email);
        if (optionalUser.isEmpty()) {
            sendResponse(response, ErrorCode.USER_NOT_FOUND.getHttpStatus(), ErrorCode.USER_NOT_FOUND.getMessage());
            return;
        }
        Users user = optionalUser.get();

        // 계정이 이미 잠금 상태인 경우
        if (user.isLoginLock()) {
            log.error("로그인 잠금된 아이디입니다.: {} ", email);
            sendResponse(response, ErrorCode.LOGIN_LOCKED.getHttpStatus(), ErrorCode.LOGIN_LOCKED.getMessage());
            return;
        }

        // 현재 실패 횟수로 계정 잠금 여부 검증 (MAX_ATTEMPTS 이상이면 잠금)
        if (user.getLoginAttempts() >= MAX_ATTEMPTS) {
            user.setLoginLock(true);
            updateUserState(response, user);
            sendResponse(response, ErrorCode.LOGIN_LOCKED.getHttpStatus(), ErrorCode.LOGIN_LOCKED.getMessage());
            return;
        }

        // 실패 횟수 증가
        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts); // 로그인 실패 횟수 저장
        user.setLastFailedLogin(Instant.now()); // 마지막 실패 시간 갱신

        // 실패 횟수가 MAX_ATTEMPTS에 도달하면 잠금 처리
        if (attempts >= MAX_ATTEMPTS) {
            user.setLoginLock(true);
            updateUserState(response, user);
            sendResponse(response, ErrorCode.LOGIN_LOCKED.getHttpStatus(), ErrorCode.LOGIN_LOCKED.getMessage());
            return;
        }

        updateUserState(response, user);
        sendResponse(response, ErrorCode.LOGIN_FAILED.getHttpStatus(), ErrorCode.LOGIN_FAILED.getMessage());
    }

    private void sendResponse(HttpServletResponse response, HttpStatus statusCode, String message) throws IOException {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setHttpStatus(statusCode);
        loginResponse.setMessage(message);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
    }

    private void updateUserState(HttpServletResponse response, Users user) throws IOException {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("로그인 가능 상태 여부 수정 중 오류가 발생하였습니다.: {}", user.getEmail(), e);
            sendResponse(response, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
}
