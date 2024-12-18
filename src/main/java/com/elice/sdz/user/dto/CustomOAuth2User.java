package com.elice.sdz.user.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final Oauth2DTO oauth2DTO;
    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", oauth2DTO.getEmail());
        attributes.put("userName", oauth2DTO.getUserName());
        attributes.put("authorities", oauth2DTO.getAuthorities());
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(oauth2DTO.getAuthorities()));
        return collection;
    }

    public String getUserId() {
        return oauth2DTO.getEmail();
    }

    @Override
    public String getName() {
        return oauth2DTO.getUserName();
    }
}
