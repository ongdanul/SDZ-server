package com.elice.sdz.user.service;

import com.elice.sdz.global.config.CookieUtils;
import com.elice.sdz.global.jwt.JWTUtil;
import com.elice.sdz.user.entity.RefreshToken;
import com.elice.sdz.user.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

import static com.elice.sdz.global.config.SecurityConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    public boolean reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refresh = CookieUtils.getCookieValue(request, "refresh");

        if (refresh == null) {
            CookieUtils.deleteCookie(response,"access");
            return false;
        }

        if (!validateRefreshToken(refresh, response)) {
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
    private boolean validateRefreshToken(String refresh, HttpServletResponse response) throws IOException {
        try {
            if (jwtUtil.isExpired(refresh)) {
                log.warn("Expired refresh token: {}", refresh);
                sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Expired refresh token");
                return false;
            }

            if (!jwtUtil.isValidCategory(refresh, "refresh")) {
                log.warn("Invalid token category: {}", refresh);
                sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token category");
                return false;
            }

            if (!refreshRepository.existsByRefresh(refresh)) {
                log.warn("Refresh token not found in DB: {}", refresh);
                sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Refresh token not found in DB");
                return false;
            }

        } catch (ExpiredJwtException e) {
            log.warn("Expired refresh token: {}", refresh);
            sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Expired refresh token");
            return false;
        } catch (Exception e) {
            log.error("Error validating refresh token: {}", refresh, e);
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Token validation error");
            return false;
        }

        return true;
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        JSONObject json = new JSONObject();
        json.put("message", message);

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
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