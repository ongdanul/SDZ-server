package com.elice.sdz.global.jwt;

import com.elice.sdz.global.util.CookieUtil;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.CustomOAuth2User;
import com.elice.sdz.user.dto.response.LoginResponse;
import com.elice.sdz.user.entity.RefreshToken;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.repository.RefreshRepository;
import com.elice.sdz.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.elice.sdz.global.config.SecurityConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private  final UserRepository userRepository;

    @Value("${spring.targetUrl}")
    String targetUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User)authentication.getPrincipal();
        String email = customOAuth2User.getEmail();

        Optional<Users> optionalUser = userRepository.findById(email);
        if (optionalUser.isEmpty()) {
            log.info("회원이 존재하지 않습니다.");
            response.sendRedirect(targetUrl);
            return;
        }
        Users user = optionalUser.get();
        if (user.isDeactivated()) {
            log.info("회원 탈퇴된 아이디입니다: {}", user.getEmail());
            response.sendRedirect(targetUrl);
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String auth = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSING_AUTHORIZATION));

        String loginType = "social";

        String access = jwtUtil.createJwt(ACCESS_TOKEN_NAME, email, auth, loginType, ACCESS_TOKEN_EXPIRATION);
        String refresh = jwtUtil.createJwt(REFRESH_TOKEN_NAME, email, auth, loginType, REFRESH_TOKEN_EXPIRATION);

        addRefreshToken(email, refresh);

        response.setHeader("Authorization", "Bearer " + access);
        CookieUtil.createCookie(response,REFRESH_COOKIE_NAME, refresh, REFRESH_COOKIE_EXPIRATION);
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(targetUrl);
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
