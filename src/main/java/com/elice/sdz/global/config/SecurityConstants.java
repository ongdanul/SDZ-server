package com.elice.sdz.global.config;

public class SecurityConstants {
    public static final long ACCESS_TOKEN_EXPIRATION = 3600000L;  //1hours
    public static final long REFRESH_TOKEN_EXPIRATION = 604800000L;  //1week
    public static final int REFRESH_COOKIE_EXPIRATION = 3 * 24 * 60 * 60; //3days
    public static final int REMEMBER_ME_EXPIRATION =  7 * 24 * 60 * 60; //1week
    public static final int REMEMBER_ID_EXPIRATION = 30 * 24 * 60 * 60; //30days
    public static final long RESET_PERIOD = 3 * 24 * 60 * 60 * 1000L; //3days
}