package com.elice.sdz.global.jwt;

import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import com.elice.sdz.user.dto.CustomUserDetails;
import com.elice.sdz.user.entity.Users;
import com.elice.sdz.user.service.ReissueService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private  final ReissueService reissueService;

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

            if (!jwtUtil.isValidCategory(accessToken, "access")) {
                throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
            }

            Users user = new Users();
            user.setUserId(jwtUtil.getUsername(accessToken));
            user.setUserAuth(Users.Auth.valueOf(jwtUtil.getRole(accessToken)));

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (ExpiredJwtException e) {
            log.warn("만료된 엑세스 토큰입니다.: {}", accessToken);
            throw new CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 오류가 발생하였습니다.:", e);
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        filterChain.doFilter(request, response);
    }
}
