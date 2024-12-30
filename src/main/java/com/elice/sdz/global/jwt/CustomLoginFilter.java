package com.elice.sdz.global.jwt;

import com.elice.sdz.global.util.CookieUtil;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.request.LoginRequest;
import com.elice.sdz.user.dto.response.LoginResponse;
import com.elice.sdz.user.entity.RefreshToken;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.RefreshRepository;
import com.elice.sdz.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.elice.sdz.global.config.SecurityConstants.*;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public CustomLoginFilter(JWTUtil jwtUtil, AuthenticationManager authenticationManager, UserRepository userRepository, RefreshRepository refreshRepository, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        setFilterProcessesUrl("/api/user/loginProcess");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest;

        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            request.setAttribute("loginRequest", loginRequest);

        } catch (IOException e) {
            log.error("JSON 파싱 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_LOGIN_REQUEST);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), null);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException, ServletException {
        LoginRequest loginRequest = (LoginRequest) request.getAttribute("loginRequest");
        if (loginRequest == null) {
            sendResponse(response, ErrorCode.INVALID_LOGIN_REQUEST.getHttpStatus(), ErrorCode.INVALID_LOGIN_REQUEST.getMessage());
            return;
        }

        String email = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Optional<String> optionalRole = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority);
        if (optionalRole.isEmpty()) {
            sendResponse(response, ErrorCode.MISSING_AUTHORIZATION.getHttpStatus(), ErrorCode.MISSING_AUTHORIZATION.getMessage());
            return;
        }
        String role = optionalRole.get();

        Optional<Users> optionalUser = userRepository.findById(email);
        if (optionalUser.isEmpty()) {
            sendResponse(response, ErrorCode.USER_NOT_FOUND.getHttpStatus(), ErrorCode.USER_NOT_FOUND.getMessage());
            return;
        }
        Users user = optionalUser.get();

        String loginType = user.isSocial() ? "social" : "local";

        if (isUserLoginLocked(response, user)) {
            return;
        }

        resetLoginAttempts(user);

        String access = jwtUtil.createJwt(ACCESS_TOKEN_NAME, email, role, loginType,ACCESS_TOKEN_EXPIRATION);
        String refresh = jwtUtil.createJwt(REFRESH_TOKEN_NAME, email, role, loginType, REFRESH_TOKEN_EXPIRATION);

        addRefreshToken(email, refresh);

        response.setHeader("Authorization", "Bearer " + access);
        CookieUtil.createCookie(response,REFRESH_COOKIE_NAME, refresh, REFRESH_COOKIE_EXPIRATION);
        sendResponse(response, HttpStatus.valueOf(HttpStatus.OK.value()), "로그인이 성공적으로 처리되었습니다.");

        handleCookie(response, loginRequest, email, refresh);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        customAuthenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }

    private boolean isUserLoginLocked(HttpServletResponse response, Users user) throws IOException {
        if (user.isLoginLock()) {
            log.info("회원 아이디가 잠금 상태입니다: {}", user.getEmail());
            sendResponse(response, ErrorCode.LOGIN_LOCKED.getHttpStatus(), ErrorCode.LOGIN_LOCKED.getMessage());
            return true;
        }
        return false;
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

    private void resetLoginAttempts(Users user) {
        if (user != null && user.getLoginAttempts() > 0) {
            user.setLoginAttempts(0);
            user.setLastFailedLogin(null);
            try {
                userRepository.save(user);
            } catch (Exception e) {
                log.error("회원 로그인 시도 횟수 초기화 중 오류 발생: {}", user.getEmail(), e);
            }
        }
    }

    private void handleCookie(HttpServletResponse response, LoginRequest loginRequest, String email, String refresh) {
        if (loginRequest.isRememberId()) {
            String encodedEmail = Base64.getEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
            CookieUtil.createCookie(response, REMEMBER_ID_COOKIE_NAME, encodedEmail, REMEMBER_ID_EXPIRATION);
        } else {
            CookieUtil.deleteCookie(response, REMEMBER_ID_COOKIE_NAME);
        }

        if (loginRequest.isRememberMe()) {
            CookieUtil.createCookie(response, REMEMBER_ME_COOKIE_NAME, refresh, REMEMBER_ME_EXPIRATION);
        }
    }

    private void addRefreshToken(String email, String refresh) {
        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION);
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshToken);
    }
}
