package com.elice.sdz.global.jwt;

import com.elice.sdz.global.config.CookieUtils;
import com.elice.sdz.user.entity.RefreshToken;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.RefreshRepository;
import com.elice.sdz.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        String userName = obtainUsername(request);
        String password = obtainPassword(request);

        boolean rememberId = "true".equals(request.getParameter("rememberId"));
        boolean rememberMe = "true".equals(request.getParameter("rememberMe"));

        String rememberIdString = Boolean.toString(rememberId); //Test
        String rememberMeString = Boolean.toString(rememberMe); //Test

        log.info("Test - rememberId: {}, rememberMe: {}", rememberIdString, rememberMeString); //Test

        Map<String, Boolean> loginDetails = new HashMap<>();
        loginDetails.put("rememberId", rememberId);
        loginDetails.put("rememberMe", rememberMe);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, password, null);
        authToken.setDetails(loginDetails);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String userId = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("Authorization is missing."));

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));

        if (isUserLoginLocked(response, user)) {
            return;
        }

        resetLoginAttempts(user);

        String access = jwtUtil.createJwt("access", userId, role, ACCESS_TOKEN_EXPIRATION);
        String refresh = jwtUtil.createJwt("refresh", userId, role, REFRESH_TOKEN_EXPIRATION);

        addRefreshToken(userId, refresh);

        response.setHeader("Authorization", "Bearer " + access);
        CookieUtils.createCookies(response,"refresh", refresh, REFRESH_COOKIE_EXPIRATION);
        response.setStatus(HttpStatus.OK.value());

        handleRememberIdCookie(response, authentication, userId);

        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        customAuthenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }

    private boolean isUserLoginLocked(HttpServletResponse response, Users user) throws IOException {
        if (user.isLoginLock()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> json = new HashMap<>();
            json.put("message", "LOGIN_LOCKED");

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(json));
            log.info("User account is locked: {}", user.getUserId());
            return true;
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
                log.error("Error occurred while resetting login attempts for user: {}", user.getUserId(), e);
            }
        }
    }

    private void handleRememberIdCookie(HttpServletResponse response, Authentication authentication, String userId) {
        @SuppressWarnings("unchecked")
        Map<String, Boolean> loginDetails = (Map<String, Boolean>) authentication.getDetails();
        Boolean rememberId = loginDetails.getOrDefault("rememberId", false);

        Cookie rememberIdCookie;
        if (Boolean.TRUE.equals(rememberId)) {
            String encodedUserId = Base64.getEncoder().encodeToString(userId.getBytes(StandardCharsets.UTF_8));
            rememberIdCookie = new Cookie("remember-id", encodedUserId);
            rememberIdCookie.setMaxAge(REMEMBER_ID_EXPIRATION);
        } else {
            rememberIdCookie = new Cookie("remember-id", null);
            rememberIdCookie.setMaxAge(0);
        }
        rememberIdCookie.setPath("/");
        response.addCookie(rememberIdCookie);

        //TODO 완성후 TEST용 로그 삭제하기
        log.info("Test - Remember ID cookie processed: {}", rememberId);
    }

    private void addRefreshToken(String username, String refresh) {
        Date date = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION);
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(username)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshToken);
    }
}
