package com.elice.sdz.global.jwt;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.CustomUserDetails;
import com.elice.sdz.user.dto.response.LoginResponse;
import com.elice.sdz.user.entity.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.elice.sdz.global.config.SecurityConstants.ACCESS_TOKEN_NAME;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.substring(7);
        if (accessToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);

            if (!jwtUtil.isValidCategory(accessToken, ACCESS_TOKEN_NAME)) {
                sendResponse(response, ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus(), ErrorCode.INVALID_ACCESS_TOKEN.getMessage());
            }

            Users user = new Users();
            user.setEmail(jwtUtil.getEmail(accessToken));
            user.setUserAuth(Users.Auth.valueOf(jwtUtil.getAuth(accessToken)));

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (ExpiredJwtException e) {
            log.warn("만료된 엑세스 토큰입니다.: {}", accessToken);
            sendResponse(response, ErrorCode.EXPIRED_ACCESS_TOKEN.getHttpStatus(), ErrorCode.EXPIRED_ACCESS_TOKEN.getMessage());
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 오류가 발생하였습니다.:", e);
            sendResponse(response, ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus(), ErrorCode.INVALID_ACCESS_TOKEN.getMessage());
        }

        filterChain.doFilter(request, response);
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
