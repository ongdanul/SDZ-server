package com.elice.sdz.user.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
