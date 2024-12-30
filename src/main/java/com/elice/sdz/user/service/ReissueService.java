package com.elice.sdz.user.service;

import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.global.jwt.JWTUtil;
import com.elice.sdz.global.util.CookieUtil;
import com.elice.sdz.user.dto.response.RefreshResponse;
import com.elice.sdz.user.entity.RefreshToken;
import com.elice.sdz.user.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> cookieValue = getCookieValue(request, "refresh");
        if(cookieValue.isEmpty()){
            log.warn("리프레시 토큰을 찾을 수 없습니다.");
            sendResponse(response, ErrorCode.INVALID_REFRESH_COOKIE.getHttpStatus(), ErrorCode.INVALID_REFRESH_COOKIE.getMessage());
            return;
        }
        String refresh = cookieValue.get();

        if (!validateRefreshToken(response, refresh)) {
            return;
        }

        String email = jwtUtil.getEmail(refresh);
        String auth = jwtUtil.getAuth(refresh);
        String loginType = jwtUtil.getLoginType(refresh);

        String newAccessToken = jwtUtil.createJwt(ACCESS_TOKEN_NAME, email, auth, loginType, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwt(REFRESH_TOKEN_NAME, email, auth, loginType, REFRESH_TOKEN_EXPIRATION);

        String newExpiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION).toString();
        int updatedRef = refreshRepository.updateRefreshToken(email, newRefreshToken, newExpiration);

        if (updatedRef == 0) {
            log.warn("리프레시 토큰 업데이트에 실패했습니다. 새로 추가합니다.");
            addRefreshToken(email, newRefreshToken);
        }

        CookieUtil.deleteCookie( response, REFRESH_COOKIE_NAME);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        CookieUtil.createCookie(response,REFRESH_COOKIE_NAME, newRefreshToken, REFRESH_COOKIE_EXPIRATION);
        sendResponse(response, HttpStatus.valueOf(HttpStatus.OK.value()), "엑세스 토큰이 성공적으로 재발급되었습니다.");
    }

    private boolean validateRefreshToken(HttpServletResponse response, String refresh) throws IOException {
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 리프레시 토큰입니다.: {}", refresh);
            sendResponse(response, ErrorCode.EXPIRED_REFRESH_TOKEN.getHttpStatus(), ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());
            return false;
        }

        if (!jwtUtil.isValidCategory(refresh, REFRESH_TOKEN_NAME)) {
            log.warn("토큰 카테고리가 불일치합니다.: {}", refresh);
            sendResponse(response, ErrorCode.INVALID_REFRESH_TOKEN.getHttpStatus(), ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
            return false;
        }

        if (Boolean.FALSE.equals(refreshRepository.existsByRefresh(refresh))) {
            log.warn("DB에 리프레시 토큰이 존재하지 않습니다: {}", refresh);
            sendResponse(response, ErrorCode.REFRESH_TOKEN_NOT_FOUND.getHttpStatus(), ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
            return false;
        }

        return true;
    }

    private void sendResponse(HttpServletResponse response, HttpStatus statusCode, String message) throws IOException {
        RefreshResponse refreshResponse = new RefreshResponse();
        refreshResponse.setHttpStatus(statusCode);
        refreshResponse.setMessage(message);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(refreshResponse));
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