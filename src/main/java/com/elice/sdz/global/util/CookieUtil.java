package com.elice.sdz.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtil {
    public static Optional<String> getCookieValue(HttpServletRequest request, String name) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(name))
                        .map(Cookie::getValue)
                        .findFirst());
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(cookieName))
                        .findFirst());
    }

    public static void createCookie(HttpServletResponse response, String cookieName, String value, int maxAge) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setPath("/");
        cookie.setSecure(true);
//        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    public static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
