package com.elice.sdz.user.service;

import com.elice.sdz.global.util.CookieUtil;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.global.jwt.JWTUtil;
import com.elice.sdz.user.entity.RefreshToken;
import com.elice.sdz.user.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.elice.sdz.global.config.SecurityConstants.*;
import static com.elice.sdz.global.util.CookieUtil.getCookieValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    public boolean reissue(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> cookieValue = getCookieValue(request, "refresh");
        String refresh = cookieValue.orElseThrow(() -> {
            log.warn("리프레시 토큰을 찾을 수 없습니다.");
            return new CustomException(ErrorCode.INVALID_REFRESH_COOKIE);
        });

        if (!validateRefreshToken(refresh)) {
            return false;
        }

        String email = jwtUtil.getEmail(refresh);
        String auth = jwtUtil.getAuth(refresh);
        String loginType = jwtUtil.getLoginType(refresh);


        String newAccessToken = jwtUtil.createJwt(ACCESS_TOKEN_NAME, email, auth, loginType, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwt(REFRESH_TOKEN_NAME, email, auth, loginType, REFRESH_TOKEN_EXPIRATION);

        refreshRepository.deleteByRefresh(refresh);
        addRefreshToken(email, newRefreshToken);

        CookieUtil.deleteCookie( response, REFRESH_COOKIE_NAME);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        CookieUtil.createCookie(response,REFRESH_COOKIE_NAME, newRefreshToken, REFRESH_COOKIE_EXPIRATION);

        return true;
    }

    private boolean validateRefreshToken(String refresh) {
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 리프레시 토큰입니다.: {}", refresh);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!jwtUtil.isValidCategory(refresh, REFRESH_TOKEN_NAME)) {
            log.warn("토큰 카테고리가 불일치합니다.: {}", refresh);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (Boolean.FALSE.equals(refreshRepository.existsByRefresh(refresh))) {
            log.warn("DB에 리프레시 토큰이 존재하지 않습니다: {}", refresh);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return true;
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