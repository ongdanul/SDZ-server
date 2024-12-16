package com.elice.sdz.global.jwt;

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
import net.minidev.json.JSONObject;
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
            if (jwtUtil.isExpired(accessToken)) {
                handleExpiredToken(request, response);
                return;
            }

            if (!jwtUtil.isValidCategory(accessToken, "access")) {
                handleInvalidToken(response, "Invalid access token");
                return;
            }

            Users user = new Users();
            user.setUserId(jwtUtil.getUsername(accessToken));
            user.setUserAuth(Users.Auth.valueOf(jwtUtil.getRole(accessToken)));

            CustomUserDetails customUserDetails = new CustomUserDetails(user, jwtUtil);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e) {
            log.warn("Expired access token: {}", accessToken);
            handleExpiredToken(request, response);
            return;
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            handleInvalidToken(response, "Token validation error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean reissued = reissueService.reissue(request, response);
        if (reissued) {
            sendJsonResponse(response, HttpServletResponse.SC_OK, "Access token reissued successfully");
        } else {
            sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Refresh token is invalid or expired");
        }
    }

    private void handleInvalidToken(HttpServletResponse response, String errorMessage) throws IOException {
        sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        JSONObject json = new JSONObject();
        json.put("message", message);

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}
