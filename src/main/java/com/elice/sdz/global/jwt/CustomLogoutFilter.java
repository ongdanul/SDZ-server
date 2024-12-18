package com.elice.sdz.global.jwt;

import com.elice.sdz.global.config.CookieUtils;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
            log.warn("POST 메소드가 아닙니다.");
            throw new CustomException(ErrorCode.METHOD_NOT_ALLOWED);
        }

        String refresh = CookieUtils.getCookieValue(request, "refresh");
        if (refresh == null) {
            log.warn("로그아웃 진행 중 리프레시 토큰을 찾을 수 없습니다.");
            throw new CustomException(ErrorCode.INVALID_REFRESH_COOKIE);
        }

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

            refreshRepository.deleteByRefresh(refresh);
            CookieUtils.deleteCookie(response, refresh);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (ExpiredJwtException e) {
            log.warn("만료된 리프레시 토큰입니다.: {}", refresh);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 오류가 발생하였습니다.:", e);
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }
}
