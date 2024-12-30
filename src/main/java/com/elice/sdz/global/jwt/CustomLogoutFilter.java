package com.elice.sdz.global.jwt;

import com.elice.sdz.global.util.CookieUtil;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.response.LoginResponse;
import com.elice.sdz.user.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

import static com.elice.sdz.global.config.SecurityConstants.REFRESH_COOKIE_NAME;
import static com.elice.sdz.global.config.SecurityConstants.REMEMBER_ME_COOKIE_NAME;
import static com.elice.sdz.global.util.CookieUtil.getCookieValue;

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
        if (!requestUri.matches("^/api/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            log.warn("POST 메소드가 아닙니다.");
            sendResponse(response, ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus(), ErrorCode.METHOD_NOT_ALLOWED.getMessage());
        }

        Optional<String> cookieValue = getCookieValue(request, "refresh");
        if(cookieValue.isEmpty()){
            log.warn("로그아웃 진행 중 리프레시 토큰을 찾을 수 없습니다.");
            sendResponse(response, ErrorCode.INVALID_REFRESH_COOKIE.getHttpStatus(), ErrorCode.INVALID_REFRESH_COOKIE.getMessage());
            return;
        }
        String refresh = cookieValue.get();

        try {
            jwtUtil.isExpired(refresh);

            if (!jwtUtil.isValidCategory(refresh, "refresh")) {
                log.warn("토큰 카테고리가 불일치합니다.: {}", refresh);
                sendResponse(response, ErrorCode.INVALID_REFRESH_TOKEN.getHttpStatus(), ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
            }

            if (Boolean.FALSE.equals(refreshRepository.existsByRefresh(refresh))) {
                log.warn("DB에 리프레시 토큰이 존재하지 않습니다: {}", refresh);
                sendResponse(response, ErrorCode.REFRESH_TOKEN_NOT_FOUND.getHttpStatus(), ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
            }

            refreshRepository.deleteByRefresh(refresh);
            CookieUtil.deleteCookie(response, REFRESH_COOKIE_NAME);
            CookieUtil.deleteCookie(response, REMEMBER_ME_COOKIE_NAME);
            sendResponse(response, HttpStatus.valueOf(HttpStatus.OK.value()), "로그아웃이 성공적으로 처리되었습니다.");

        } catch (ExpiredJwtException e) {
            log.warn("만료된 리프레시 토큰입니다.: {}", refresh);
            sendResponse(response, ErrorCode.EXPIRED_REFRESH_TOKEN.getHttpStatus(), ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage());
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 오류가 발생하였습니다.:", e);
            sendResponse(response, ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus(), ErrorCode.INVALID_ACCESS_TOKEN.getMessage());
        }
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
}
