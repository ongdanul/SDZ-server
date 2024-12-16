package com.elice.sdz.global.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomOauth2Exception extends AuthenticationException {
    public CustomOauth2Exception(String message) {
        super(message);
    }
}
