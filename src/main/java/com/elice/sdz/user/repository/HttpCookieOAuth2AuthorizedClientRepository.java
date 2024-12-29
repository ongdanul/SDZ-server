package com.elice.sdz.user.repository;

import com.elice.sdz.global.util.Aes256;
import com.elice.sdz.global.util.CookieUtil;
import com.elice.sdz.global.exception.CustomException;
import com.elice.sdz.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

import static com.elice.sdz.global.config.SecurityConstants.OAUTH2_COOKIE_NAME;
import static com.elice.sdz.global.config.SecurityConstants.OAUTH_COOKIE_EXPIRY;
import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpCookieOAuth2AuthorizedClientRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

        @Override
        public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
            return getCookie(request);
        }

        @Override
        public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
                HttpServletResponse response) {
            if (isNull(authorizationRequest)) {
                removeAuthorizationRequest(request, response);
                return;
            }

            CookieUtil.createCookie(response, OAUTH2_COOKIE_NAME, encrypt(authorizationRequest), OAUTH_COOKIE_EXPIRY);
        }

        @Override
        public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                HttpServletResponse response) {
            OAuth2AuthorizationRequest oAuth2AuthorizationRequest = getCookie(request);
            CookieUtil.deleteCookie(response, OAUTH2_COOKIE_NAME);
            return oAuth2AuthorizationRequest;
        }

        private OAuth2AuthorizationRequest getCookie(HttpServletRequest request) {
            return CookieUtil.getCookie(request, OAUTH2_COOKIE_NAME).map(this::decrypt).orElse(null);
        }

        private String encrypt(OAuth2AuthorizationRequest authorizationRequest) {
            byte[] bytes = SerializationUtils.serialize(authorizationRequest);
            return Aes256.encrypt(bytes);
        }

        private OAuth2AuthorizationRequest decrypt(Cookie cookie) {
            byte[] bytes = Aes256.decrypt(cookie.getValue().getBytes(StandardCharsets.UTF_8));
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return (OAuth2AuthorizationRequest) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new CustomException(ErrorCode.DESERIALIZATION_FAILED);
            }
        }
}
