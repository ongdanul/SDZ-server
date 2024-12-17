package com.elice.sdz.user.service;

import com.elice.sdz.global.config.CookieUtils;
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

import static com.elice.sdz.global.config.SecurityConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    public boolean reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = CookieUtils.getCookieValue(request, "refresh");

        if (refresh == null) {
            CookieUtils.deleteCookie(response,"access");
            return false;
        }

        if (!validateRefreshToken(refresh)) {
            return false;
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccessToken = jwtUtil.createJwt("access", username, role, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, REFRESH_TOKEN_EXPIRATION);

        refreshRepository.deleteByRefresh(refresh);
        addRefreshToken(username, newRefreshToken);

        CookieUtils.deleteCookies(request, response);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        CookieUtils.createCookies(response,"refresh", newRefreshToken, REFRESH_COOKIE_EXPIRATION);

        return true;
    }
    private boolean validateRefreshToken(String refresh) {
        try {
            jwtUtil.isExpired(refresh);

            if (!jwtUtil.isValidCategory(refresh, "refresh")) {
                log.warn("토큰 카테고리가 불일치합니다.: {}", refresh);
                throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
            }

            if (Boolean.FALSE.equals(refreshRepository.existsByRefresh(refresh))) {
                log.warn("DB에 리프레시 토큰이 존재하지 않습니다: {}", refresh);
                throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }

        } catch (ExpiredJwtException e) {
            log.warn("만료된 리프레시 토큰입니다.: {}", refresh);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
            log.error("리프레시 토큰 검증 중 오류가 발생하였습니다.: {}", refresh, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return true;
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