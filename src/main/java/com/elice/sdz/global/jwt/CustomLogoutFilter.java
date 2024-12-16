package com.elice.sdz.global.jwt;

import com.elice.sdz.global.config.CookieUtils;
import com.elice.sdz.user.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter implements Filter {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            log.warn("This is not a POST method");
            sendJsonResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allowed");
            return;
        }

        String refresh = CookieUtils.getCookieValue(request, "refresh");
        if (refresh == null) {
            log.warn("No refresh token found, proceeding with logout");
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token missing");
            return;
        }

        try {
            if (jwtUtil.isExpired(refresh)) {
                log.warn("Expired refresh token: {}", refresh);
                sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Expired refresh token");
                return;
            }

            if (!jwtUtil.isValidCategory(refresh, "refresh")) {
                log.warn("Invalid token category: {}", refresh);
                sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid refresh token category");
                return;
            }

            if (!refreshRepository.existsByRefresh(refresh)) {
                log.warn("Refresh token not found in DB: {}", refresh);
                sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token not found in database");
                return;
            }

            refreshRepository.deleteByRefresh(refresh);
            CookieUtils.deleteCookie(response, refresh);
            sendJsonResponse(response, HttpServletResponse.SC_OK, "Logout successful");

        } catch (ExpiredJwtException e) {
            log.warn("Expired refresh token: {}", refresh);
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Expired refresh token");
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Token validation error");
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> json = new HashMap<>();
        json.put("message", message);

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(json));
    }
}
