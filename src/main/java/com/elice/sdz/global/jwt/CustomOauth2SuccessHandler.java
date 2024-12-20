package com.elice.sdz.global.jwt;

import com.elice.sdz.global.config.CookieUtils;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.CustomOAuth2User;
import com.elice.sdz.user.entity.RefreshToken;
import com.elice.sdz.user.repository.RefreshRepository;
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

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

import static com.elice.sdz.global.config.SecurityConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Value("${spring.targetUrl}")
    String targetUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User)authentication.getPrincipal();
        String email = customOAuth2User.getEmail();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String auth = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSING_AUTHORIZATION));

        String access = jwtUtil.createJwt("access", email, auth, ACCESS_TOKEN_EXPIRATION);
        String refresh = jwtUtil.createJwt("refresh", email, auth, REFRESH_TOKEN_EXPIRATION);

        addRefreshToken(email, refresh);

        response.setHeader("Authorization", "Bearer " + access);
        CookieUtils.createCookies(response,"refresh", refresh, REFRESH_COOKIE_EXPIRATION);
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(targetUrl);
        log.info("Test - Oauth login success");
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
