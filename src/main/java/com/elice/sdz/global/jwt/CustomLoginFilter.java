package com.elice.sdz.global.jwt;

import com.elice.sdz.global.util.CookieUtil;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.LoginRequest;
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
        //TODO 로그인 기능 완성후 TEST용 로그 삭제하기
        log.info("Test - LoginFilter is being called"); //Test

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest = null;

        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            request.setAttribute("loginRequest", loginRequest);

        } catch (IOException e) {
            log.error("JSON 파싱 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_LOGIN_REQUEST);
        }

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        boolean rememberId = loginRequest.isRememberId();
        boolean rememberMe = loginRequest.isRememberMe();

        log.info("Attempting authentication - email: {}, password: {}", email, password);

        String rememberIdString = Boolean.toString(rememberId); //Test
        String rememberMeString = Boolean.toString(rememberMe); //Test

        log.info("Test - rememberId: {}, rememberMe: {}", rememberIdString, rememberMeString); //Test

        Map<String, Boolean> loginDetails = new HashMap<>();
        loginDetails.put("rememberId", rememberId);
        loginDetails.put("rememberMe", rememberMe);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        authToken.setDetails(loginDetails);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSING_AUTHORIZATION));

        Users user = userRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (isUserLoginLocked(user)) {
            return;
        }

        resetLoginAttempts(user);

        String access = jwtUtil.createJwt(ACCESS_TOKEN_NAME, email, role, ACCESS_TOKEN_EXPIRATION);
        String refresh = jwtUtil.createJwt(REFRESH_TOKEN_NAME, email, role, REFRESH_TOKEN_EXPIRATION);

        addRefreshToken(email, refresh);

        response.setHeader("Authorization", "Bearer " + access);
        CookieUtil.createCookie(response,REFRESH_COOKIE_NAME, refresh, REFRESH_COOKIE_EXPIRATION);
        response.setStatus(HttpStatus.OK.value());

        handleCookie(response, authentication, email, refresh);

        log.info("Test - successfulAuthentication");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        customAuthenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }

    private boolean isUserLoginLocked(Users user) {
        if (user.isLoginLock()) {
            log.info("회원 아이디가 잠금 상태입니다: {}", user.getEmail());
            throw new CustomException(ErrorCode.LOGIN_LOCKED);
        }
        return false;
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

    private void handleCookie(HttpServletResponse response, Authentication authentication, String email, String refresh) {
        @SuppressWarnings("unchecked")
        Map<String, Boolean> loginDetails = (Map<String, Boolean>) authentication.getDetails();
        Boolean rememberId = loginDetails.getOrDefault("rememberId", false);
        Boolean rememberMe = loginDetails.getOrDefault("rememberMe", false);

        if (Boolean.TRUE.equals(rememberId)) {
            String encodedEmail = Base64.getEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
            CookieUtil.createCookie(response, REMEMBER_ID_COOKIE_NAME, encodedEmail, REMEMBER_ID_EXPIRATION);
        } else {
            CookieUtil.deleteCookie(response, REMEMBER_ID_COOKIE_NAME);
        }

        if (Boolean.TRUE.equals(rememberMe)) {
            CookieUtil.createCookie(response, REMEMBER_ME_COOKIE_NAME, refresh, REMEMBER_ME_EXPIRATION);
        }

        //TODO 완성후 TEST용 로그 삭제하기
        log.info("Test - Remember ID cookie processed: {}", rememberId);
        log.info("Test - remember Me cookie processed: {}", rememberMe);
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
