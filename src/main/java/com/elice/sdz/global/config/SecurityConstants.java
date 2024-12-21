package com.elice.sdz.global.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {
    public static final String ACCESS_TOKEN_NAME = "access";
    public static final String REFRESH_TOKEN_NAME = "refresh";
    public static final long ACCESS_TOKEN_EXPIRATION = 3600000L;  //1hours
    public static final long REFRESH_TOKEN_EXPIRATION = 604800000L;  //1week
    public static final String REFRESH_COOKIE_NAME = "refresh";
    public static final int REFRESH_COOKIE_EXPIRATION = 3 * 24 * 60 * 60; //3days
    public static final String REMEMBER_ME_COOKIE_NAME = "remember-me";
    public static final String REMEMBER_ID_COOKIE_NAME = "remember-id";
    public static final int REMEMBER_ME_EXPIRATION =  7 * 24 * 60 * 60; //1week
    public static final int REMEMBER_ID_EXPIRATION = 30 * 24 * 60 * 60; //30days
    public static final long RESET_PERIOD = 3 * 24 * 60 * 60 * 1000L; //3days
    public static final String OAUTH2_COOKIE_NAME = "OAUTH2_AUTHORIZATION_REQUEST";
    public static final int OAUTH_COOKIE_EXPIRY = 5 * 60; //5minutes;
}